package Utils
import NewVersion.ProtocolField
import kotlin.experimental.and

object  DataUtil {
    @JvmStatic
     fun getType(byte: Byte):Int{
        val b = byte and 0xF0.toByte()
        if (b==0x00.toByte()){
            if (byte== ProtocolField.onlineSuccess)
                return 1
            return 0
        }else{
            when(b){
                0x10.toByte() -> return 1
                0X20.toByte() -> return 2
                0x30.toByte() -> return 3
            }
            return -1
        }
    }
}