package com.radix.app

import kotlin.math.sqrt

/** Offline-first gamification: XP, levels, same-device leaderboard, daily challenge. Pure. */
internal object GameEngine {
    data class Profile(val name:String,val xp:Int)
    fun xpAward(tier:Int,streakDays:Int):Int{require(tier in 0..2){"Unknown tier."};return listOf(10,20,30)[tier]+minOf(maxOf(streakDays,0),7)*2}
    fun level(xp:Int):Int{require(xp>=0){"XP cannot be negative."};return sqrt(xp/50.0).toInt()}
    fun levelName(level:Int):String{require(level>=0){"Level cannot be negative."};return listOf("Novice","Apprentice","Builder","Thinker","Solver","Designer","Architect","Master")[minOf(level,7)]+(if(level>7)" +${level-7}" else "")}
    fun xpForLevel(level:Int):Int{require(level>=0){"Level cannot be negative."};return 50*level*level}
    fun progressToNext(xp:Int):Pair<Int,Int>{val l=level(xp);val base=xpForLevel(l);return (xp-base) to (xpForLevel(l+1)-base)}
    fun dailyIndex(epochDay:Long,size:Int):Int{require(size>0){"Empty catalog."};return Math.floorMod(epochDay,size.toLong()).toInt()}
    fun cleanName(name:String):String{val c=name.trim().replace(Regex("\\s+")," ").take(24);require(c.isNotEmpty()&&c.none{it.code<32}){"Name needs 1–24 printable characters."};return c}
    fun rank(profiles:List<Profile>):List<Profile> = profiles.sortedWith(compareByDescending<Profile>{it.xp}.thenBy{it.name})
    fun scoreLines(name:String,xp:Int,mastered:Int,total:Int,streak:Int):List<String>{
        val l=level(xp);val (into,needed)=progressToNext(xp)
        return listOf("$name · Level $l ${levelName(l)}","$xp XP ($into/$needed to Level ${l+1})","$mastered/$total practice cards mastered","Streak: ${if(streak<=0)"warming up" else "$streak day${if(streak==1)"" else "s"} 🔥"}")
    }
}
