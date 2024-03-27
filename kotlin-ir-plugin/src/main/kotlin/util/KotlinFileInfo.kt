package util

import org.clyze.persistent.metadata.FileInfo
import org.clyze.persistent.metadata.jvm.JvmMetadata
import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.ir.IrFileEntry

class KotlinFileInfo(packageName :String, inputName :String, private val fileEntry : IrFileEntry, source :String): FileInfo(packageName,inputName,fileEntry.toString(),source,JvmMetadata()) {
    fun getPosition(startOffset: Int, name: String): Position {
        if(source.substring(startOffset,startOffset+name.length) != name) return Position(-1,-1,-1,-1)
        val info = fileEntry.getSourceRangeInfo(startOffset, startOffset+name.length)
        return Position(
                info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
                info.endColumnNumber.toLong()
        )
    }

    fun getPosition(startOffset: Int, endOffset: Int): Position {
        val info = fileEntry.getSourceRangeInfo(startOffset, startOffset+endOffset)
        return Position(
                info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
                info.endColumnNumber.toLong()
        )
    }
    fun seeSource(st:Int,end:Int):String{
        return source.substring(st,end)
    }
    fun skipWhitespaces(offset: Int): Int{
        var start=offset
        while(source[start].isWhitespace()) start+=1
        return start
    }

    fun findKeyword(offset: Int, keyword: String): Int{
        return source.indexOf(keyword,offset)+keyword.length
    }

    fun existsInSources(pos: Position):Boolean{
        return pos.startLine >= 0
    }
}