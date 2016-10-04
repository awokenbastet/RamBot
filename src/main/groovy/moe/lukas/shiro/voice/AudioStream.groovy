package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import net.sourceforge.jaad.mp4.MP4Container
import net.sourceforge.jaad.mp4.api.AudioTrack
import net.sourceforge.jaad.mp4.api.Movie
import net.sourceforge.jaad.mp4.api.Track

@CompileStatic
class AudioStream {
    private volatile Track track
    private volatile RandomAccessFile raf

    public byte[] decoderInfo = null

    AudioStream(File file) {
        raf = new RandomAccessFile(file, "r")
        MP4Container container = new MP4Container(raf)
        Movie movie = container.movie

        List<Track> trackList = movie.getTracks(AudioTrack.AudioCodec.AAC)
        if (trackList.size() > 0) {
            track = trackList[0]
            decoderInfo = track.decoderSpecificInfo
        }
    }

    byte[] readFrame() {
        return track.readNextFrame().data
    }

    void close() {
        raf.close()
    }
}
