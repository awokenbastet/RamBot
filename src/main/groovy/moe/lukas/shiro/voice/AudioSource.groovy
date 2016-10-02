package moe.lukas.shiro.voice

import groovy.transform.CompileStatic

import java.nio.file.FileAlreadyExistsException

@CompileStatic
interface AudioSource {
    String getSource()

    AudioInfo getInfo()

    AudioStream asStream()

    File asFile(String path, boolean deleteOnExists) throws FileAlreadyExistsException, FileNotFoundException
}
