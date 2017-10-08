package Utils

import java.io.File
import java.io.IOException

fun deleteFileOrDirs(vararg paths:String):Int{
    var count = 0
    paths.forEach {
        if(deleteFun(it))
            count++
    }
    return count
}

private fun deleteFun(path:String):Boolean{
    var flag = false
    try {
        val file = File(path)
        flag =  if(file.exists()){
            if(file.isDirectory)
                file.deleteRecursively()
            else
                file.delete()
        }else
            false
    }catch (e:IOException){
        e.printStackTrace()
    }
    finally {
        return flag
    }
}