package com.radix.app

import org.junit.Assert.*
import org.junit.Test

class Radix6EnginesTest {
    @Test fun floatDecodeKnownWords(){
        val one=FloatLab.decode(0x3F800000L,32)
        assertEquals(0,one.sign);assertEquals("01111111",one.expBits);assertEquals(127,one.expRaw)
        assertEquals(127,one.bias);assertEquals(0,one.expValue);assertEquals("Normal",one.kind)
        assertEquals(1.0,one.value,0.0);assertEquals("3F800000",one.hex)
        assertEquals("+0 (positive zero)",FloatLab.decode(0L,32).kind)
        assertEquals("−0 (negative zero)",FloatLab.decode(0x80000000L,32).kind)
        assertEquals("+Infinity",FloatLab.decode(0x7F800000L,32).kind)
        assertEquals("−Infinity",FloatLab.decode(0xFF800000L,32).kind)
        assertEquals("NaN (Not a Number)",FloatLab.decode(0x7FC00000L,32).kind)
        val sub=FloatLab.decode(1L,32);assertEquals("Subnormal (denormal)",sub.kind);assertEquals(-126,sub.expValue)
        val d=FloatLab.decode(0x3FF0000000000000L,64);assertEquals(1023,d.bias);assertEquals(0,d.expValue);assertEquals(1.0,d.value,0.0)
        assertThrows(IllegalArgumentException::class.java){FloatLab.decode(0L,16)}
    }
    @Test fun floatEncodeDecimalsAndSpecials(){
        assertEquals("3F800000",FloatLab.encode("1.0",32).hex)
        assertEquals("−0 (negative zero)",FloatLab.encode("-0.0",32).kind)
        assertEquals("+Infinity",FloatLab.encode("inf",32).kind)
        assertEquals("−Infinity",FloatLab.encode("-infinity",64).kind)
        assertEquals("NaN (Not a Number)",FloatLab.encode("nan",32).kind)
        val tenth=FloatLab.encode("0.1",32);assertEquals("3DCCCCCD",tenth.hex);assertTrue(tenth.steps.any{it.contains("rounding error")})
        assertThrows(IllegalArgumentException::class.java){FloatLab.encode("twelve",32)}
        assertThrows(IllegalArgumentException::class.java){FloatLab.encode("",64)}
    }
    @Test fun floatParsersAreStrict(){
        assertEquals(0L,FloatLab.parseBits("0".repeat(32),32))
        assertEquals(0x3F800000L,FloatLab.parseHex("0x3f800000",32))
        assertEquals(0x3FF0000000000000L,FloatLab.parseHex("3FF0000000000000",64))
        assertThrows(IllegalArgumentException::class.java){FloatLab.parseBits("101",32)}
        assertThrows(IllegalArgumentException::class.java){FloatLab.parseHex("XYZ",32)}
        assertThrows(IllegalArgumentException::class.java){FloatLab.parseHex("3F80",32)}
    }
    @Test fun builderPresetsAreValidAndCorrect(){
        BuilderEngine.presets().forEach{(_,board)->assertTrue(BuilderEngine.validate(board).isEmpty())}
        val xor=BuilderEngine.presetXorFromNand()
        listOf(listOf(false,false) to false,listOf(false,true) to true,listOf(true,false) to true,listOf(true,true) to false).forEach{(inputs,want)->
            assertEquals(want,BuilderEngine.evaluate(xor.copy(ext=inputs+listOf(false,false))).probe)}
        val half=BuilderEngine.presetHalfAdder()
        val full=BuilderEngine.evaluate(half.copy(ext=listOf(true,true,false,false)))
        assertEquals(false,full.probe);assertEquals(true,full.gateOut[1])
        val table=BuilderEngine.truthTable(half)
        assertEquals(listOf(0,1),table.inputs)
        assertEquals(listOf(false,true,true,false),table.rows.map{it.second})
        val mux=BuilderEngine.presetMux()
        assertEquals(true,BuilderEngine.evaluate(mux.copy(ext=listOf(false,true,false,false))).probe)
        assertEquals(false,BuilderEngine.evaluate(mux.copy(ext=listOf(false,false,true,false))).probe)
        assertEquals(true,BuilderEngine.evaluate(mux.copy(ext=listOf(true,false,true,false))).probe)
        assertEquals(false,BuilderEngine.evaluate(mux.copy(ext=listOf(true,true,false,false))).probe)
    }
    @Test fun builderValidationCatchesWiringMistakes(){
        val half=BuilderEngine.presetHalfAdder()
        assertTrue(BuilderEngine.validate(half.copy(wires=half.wires.dropLast(1))).any{it.contains("probe")})
        assertTrue(BuilderEngine.validate(half.copy(wires=half.wires.filterNot{it.toId==0&&it.toPin==1})).any{it.contains("unwired")})
        val double=half.copy(wires=half.wires+BuilderEngine.Wire(BuilderEngine.EXT_IN,2,0,0))
        assertTrue(BuilderEngine.validate(double).any{it.contains("drivers")})
        val loop=BuilderEngine.Board(listOf(BuilderEngine.Placed(0,LogicEngine.Gate.NOT,0f,0f),BuilderEngine.Placed(1,LogicEngine.Gate.NOT,0f,0f)),
            listOf(BuilderEngine.Wire(0,0,1,0),BuilderEngine.Wire(1,0,0,0),BuilderEngine.Wire(0,0,BuilderEngine.PROBE,0)),List(4){false})
        assertTrue(BuilderEngine.validate(loop).any{it.contains("Feedback")})
        assertThrows(IllegalArgumentException::class.java){BuilderEngine.evaluate(loop)}
        assertEquals(1,BuilderEngine.arity(LogicEngine.Gate.NOT));assertEquals(2,BuilderEngine.arity(LogicEngine.Gate.XOR))
        assertEquals("C",BuilderEngine.inputName(2))
    }
    @Test fun liveSimFindsTheClassicGlitch(){
        val board=BuilderEngine.Board(
            listOf(BuilderEngine.Placed(0,LogicEngine.Gate.NOT,0f,0f),BuilderEngine.Placed(1,LogicEngine.Gate.AND,0f,0f)),
            listOf(BuilderEngine.Wire(BuilderEngine.EXT_IN,0,0,0),BuilderEngine.Wire(BuilderEngine.EXT_IN,0,1,0),BuilderEngine.Wire(0,0,1,1),BuilderEngine.Wire(1,0,BuilderEngine.PROBE,0)),List(4){false})
        assertTrue(BuilderEngine.validate(board).isEmpty())
        val s=LiveSim.settle(board,0)
        assertEquals(false,s.final);assertTrue(s.glitched);assertEquals(6,s.settleNs)
        assertEquals(listOf(true,false),s.probeEvents.map{it.value});assertEquals(listOf(4,6),s.probeEvents.map{it.t})
        assertEquals(6,LiveSim.delayNs(LogicEngine.Gate.XOR));assertEquals(2,LiveSim.delayNs(LogicEngine.Gate.NOT))
    }
    @Test fun liveSimCleanTransitionsDoNotGlitch(){
        val board=BuilderEngine.Board(listOf(BuilderEngine.Placed(0,LogicEngine.Gate.AND,0f,0f)),
            listOf(BuilderEngine.Wire(BuilderEngine.EXT_IN,0,0,0),BuilderEngine.Wire(BuilderEngine.EXT_IN,1,0,1),BuilderEngine.Wire(0,0,BuilderEngine.PROBE,0)),listOf(false,true,false,false))
        val s=LiveSim.settle(board,0)
        assertEquals(true,s.final);assertEquals(false,s.glitched);assertEquals(4,s.settleNs)
        assertEquals(1,s.probeEvents.size)
    }
    @Test fun liveSimClockRunsEdgesAndHolds(){
        val dff=LogicCatalog.sequential.first{it.id=="D_FF"}
        val ticks=LiveSim.runClock(dff,listOf(true),100,2)
        assertEquals(5,ticks.size);assertEquals(listOf(0,50,100,150,200),ticks.map{it.t})
        assertEquals(listOf(false,true,false,true,false),ticks.map{it.clock})
        assertEquals(1,ticks[1].state);assertEquals(1,ticks[4].state)
        val latch=LogicCatalog.sequential.first{it.id=="SR_NOR"}
        val free=LiveSim.runClock(latch,listOf(false,false),100,1)
        assertTrue(free.all{!it.clock});assertEquals(3,free.size)
        assertThrows(IllegalArgumentException::class.java){LiveSim.runClock(dff,listOf(true),5,2)}
        assertThrows(IllegalArgumentException::class.java){LiveSim.runClock(dff,listOf(true,true),100,1)}
    }
    @Test fun gameXpLevelsAndRanks(){
        assertEquals(10,GameEngine.xpAward(0,0));assertEquals(44,GameEngine.xpAward(2,7));assertEquals(34,GameEngine.xpAward(1,100))
        assertEquals(0,GameEngine.level(0));assertEquals(0,GameEngine.level(49));assertEquals(1,GameEngine.level(50));assertEquals(2,GameEngine.level(200))
        assertEquals("Novice",GameEngine.levelName(0));assertEquals("Master",GameEngine.levelName(7));assertEquals("Master +2",GameEngine.levelName(9))
        assertEquals(200,GameEngine.xpForLevel(2));assertEquals(25 to 150,GameEngine.progressToNext(75))
        assertEquals(83,GameEngine.dailyIndex(-1L,84));assertEquals(0,GameEngine.dailyIndex(0L,5))
        assertEquals("Ada Lovelace",GameEngine.cleanName("  Ada   Lovelace  "))
        assertThrows(IllegalArgumentException::class.java){GameEngine.cleanName("   ")}
        val ranked=GameEngine.rank(listOf(GameEngine.Profile("Zed",10),GameEngine.Profile("Amy",50),GameEngine.Profile("Bob",50)))
        assertEquals(listOf("Amy","Bob","Zed"),ranked.map{it.name})
        assertTrue(GameEngine.scoreLines("Amy",75,3,108,2).any{it.contains("Level 1")})
    }
}
