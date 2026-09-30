package com.radix.app

/** One atomic user action is one undo entry; new edits discard the redo branch. */
internal class StudyHistory<T>(private val limit:Int=32) {
    init { require(limit in 1..128) }
    private val past=ArrayDeque<T>();private val future=ArrayDeque<T>()
    val canUndo get()=past.isNotEmpty();val canRedo get()=future.isNotEmpty()
    fun record(before:T,after:T){if(before==after)return;past.addLast(before);if(past.size>limit)past.removeFirst();future.clear()}
    fun undo(current:T):T? {if(past.isEmpty())return null;future.addLast(current);return past.removeLast()}
    fun redo(current:T):T? {if(future.isEmpty())return null;past.addLast(current);if(past.size>limit)past.removeFirst();return future.removeLast()}
    fun clear(){past.clear();future.clear()}
}
