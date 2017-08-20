package com.lp.flashremote.Model

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import com.lp.flashremote.beans.BaseFile
import com.lp.flashremote.beans.MusicFile
import com.lp.flashremote.beans.VideoFile
import com.lp.flashremote.fragments.FileFragment
import java.util.*

/**
 * Created by LZL on 2017/8/18.
 */

class FileManagerModel(val context: Context,val handler: Handler) {
    internal val fileURI = Uri.parse("content://media/external/file")
    internal val musicURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    internal val videoURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    internal val imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    internal val imageSelectionArgs = arrayOf("image/png", "image/jpg", "image/jpeg", "image/gif")
    internal val documentSelectionArgs = arrayOf("%.doc", "%.docx", "%.ppt", "%.pptx", "%.pdf", "%.xlsx", "%.xls", "%.txt")
    internal val zipSelectionArgs = arrayOf("%.zip", "%.rar", "%.cab", "%.gzip", "%.tar")
    internal val apkSelectionArgs = arrayOf("%.apk")

    private fun work() {
        println("startCount!!")
        val musciList = ArrayList<MusicFile>()
        val cr = context.contentResolver
        var musicCursor = cr.query(musicURI, null, null, null, null)
        while (musicCursor.moveToNext()) {
            val title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            val artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            val size = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
            val path = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            val name = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
            val duration = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            val musicFile = MusicFile(path, name, size, title, artist, duration)
            musciList.add(musicFile)
        }

        println("Music size is ${musciList.size}")
        val videoList = ArrayList<VideoFile>()
        var videoCR = cr.query(videoURI, null, null, null, null)
        while (videoCR.moveToNext()) {
            val title = videoCR.getString(videoCR.getColumnIndex(MediaStore.Video.Media.TITLE))
            val size = videoCR.getLong(videoCR.getColumnIndex(MediaStore.Video.Media.SIZE))
            val name = videoCR.getString(videoCR.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
            val duration = videoCR.getLong(videoCR.getColumnIndex(MediaStore.Video.Media.DURATION))
            val path = videoCR.getString(videoCR.getColumnIndex(MediaStore.Video.Media.DATA))
            val videoFile = VideoFile(path, size, name, title, duration)
            videoList.add(videoFile)
        }

        val imageList = ArrayList<BaseFile>()
        var imageCR = cr.query(imageURI,
                null,
                (MediaStore.Images.Media.MIME_TYPE + "=? or ") * 3 + MediaStore.Images.Media.MIME_TYPE + "=?",
                imageSelectionArgs,
                null)
        while (imageCR.moveToNext()) {
            val size = imageCR.getLong(imageCR.getColumnIndex(MediaStore.Images.Media.SIZE))
            val name = imageCR.getString(imageCR.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
            val path = imageCR.getString(imageCR.getColumnIndex(MediaStore.Images.Media.DATA))
            val imageFile = BaseFile(path, size, name)
            imageList.add(imageFile)
        }

        val docList = ArrayList<BaseFile>()
        var docCR = cr.query(fileURI,
                null,
                (MediaStore.Files.FileColumns.DATA+"=? or "+MediaStore.Files.FileColumns.DATA + " like ? or ") * 6 + MediaStore.Files.FileColumns.DATA + " like ?",
                documentSelectionArgs,
                null
        )
        while (docCR.moveToNext()) {
            val fileSize = docCR.getLong(docCR.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
            val filePath = docCR.getString(docCR.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            val params = filePath.split("/")
            val fileName = params[params.size-1]
            val baseFile = BaseFile(filePath, fileSize, fileName)
            docList.add(baseFile)
        }

        val apkList = ArrayList<BaseFile>()
        var apkCR = cr.query(
                fileURI,
                null,
                MediaStore.Files.FileColumns.DATA + " like ? ",
                apkSelectionArgs,
                null
        )
        while (apkCR.moveToNext()) {
            val fileSize = apkCR.getLong(apkCR.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
            val filePath = apkCR.getString(apkCR.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            val params = filePath.split("/")
            val fileName = params[params.size-1]
            val baseFile = BaseFile(filePath, fileSize, fileName)
            apkList.add(baseFile)
        }

        val zipList = ArrayList<BaseFile>()
        var zipCR = cr.query(
                fileURI,
                null,
                (MediaStore.Files.FileColumns.DATA + " like ? or ") * 4 + MediaStore.Files.FileColumns.DATA + " like ? ",
                zipSelectionArgs,
                null
        )
        while (zipCR.moveToNext()) {
            val fileSize = zipCR.getLong(zipCR.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
            val filePath = zipCR.getString(zipCR.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            val params = filePath.split("/")
            val fileName = params[params.size-1]
            val baseFile = BaseFile(filePath, fileSize, fileName)
            zipList.add(baseFile)
        }
        zipCR.close()
        apkCR.close()
        musicCursor.close()
        videoCR.close()
        imageCR.close()
        docCR.close()

        val msg = Message()


        FileManagerStatic.musicList = musciList.toList()
        FileManagerStatic.videoList = videoList.toList()
        FileManagerStatic.picList = imageList.toList()
        FileManagerStatic.zipList = zipList.toList()
        FileManagerStatic.apkList = apkList.toList()
        FileManagerStatic.docList = docList.toList()

        msg.what = FileFragment.REFRESH_COUNT
        handler.sendMessage(msg)
        println("count end!!")
    }

    fun startCount() {
        Thread {
            work()
        }.start()
    }
}

operator fun String.times(count: Int): String {
    return if (count > 0) {
        var builder = StringBuilder()
        var myCount = count
        while (myCount > 0) {
            builder.append(this)
            myCount--
        }
        builder.toString()
    } else {
        return this
    }
}
