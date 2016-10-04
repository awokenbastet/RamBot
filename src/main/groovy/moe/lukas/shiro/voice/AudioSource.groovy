package moe.lukas.shiro.voice

import groovy.transform.CompileStatic

import java.nio.file.FileAlreadyExistsException

@CompileStatic
class AudioSource {
    private File file

    AudioSource(File file) {
        if (file == null)
            throw new IllegalArgumentException("Provided file was null!")
        if (!file.exists())
            throw new IllegalArgumentException("Provided file does not exist!")
        if (file.isDirectory())
            throw new IllegalArgumentException("Provided file is actually a directory. Must provide a file!")
        if (!file.canRead())
            throw new IllegalArgumentException("Provided file is unreadable due to a lack of permissions")

        this.file = file
    }

    String getSource() {
        try {
            return file.getCanonicalPath()
        }
        catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

    AudioStream asStream() {
        try {
            return new AudioStream(file)
        } catch (IOException e) {
            e.printStackTrace()
            return null
        }
    }

    File asFile(String path, boolean deleteOnExists) throws FileAlreadyExistsException, FileNotFoundException {
        return null
    }

    File asFile() {
        return this.file
    }
}
