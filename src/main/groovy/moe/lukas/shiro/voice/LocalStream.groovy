package moe.lukas.shiro.voice

import java.util.regex.Matcher

class LocalStream extends AudioStream {
    private Process ffmpegProcess
    private Thread ffmpegErrGobler
    private AudioTimestamp timestamp

    LocalStream(List<String> ffmpegLaunchArgs) {
        try {
            ProcessBuilder pBuilder = new ProcessBuilder()

            pBuilder.command(ffmpegLaunchArgs)
            ffmpegProcess = pBuilder.start()

            final Process ffmpegProcessF = ffmpegProcess

            ffmpegErrGobler = new Thread("LocalStream ffmpegErrGobler") {
                @Override
                void run() {
                    try {
                        InputStream fromFFmpeg = null

                        fromFFmpeg = ffmpegProcessF.getErrorStream()
                        if (fromFFmpeg == null)

                        byte[] buffer = []
                        int amountRead = -1

                        while (!isInterrupted() && ((amountRead = fromFFmpeg.read(buffer)) > -1)) {
                            String info = new String(Arrays.copyOf(buffer, amountRead))
                            if (info.contains("time=")) {
                                Matcher m = TIME_PATTERN.matcher(info)
                                if (m.find()) {
                                    timestamp = AudioTimestamp.fromFFmpegTimestamp(m.group())
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace()
                    }
                }
            }

            ffmpegErrGobler.start()
            this.in = ffmpegProcess.getInputStream()
        }
        catch (IOException e) {
            e.printStackTrace()
            try {
                close()
            }
            catch (IOException e1) {
                e1.printStackTrace()
            }
        }
    }

    @Override
    AudioTimestamp getCurrentTimestamp() {
        return timestamp
    }

    @Override
    void close() throws IOException {
        if (this.in != null) {
            this.in.close()
            this.in = null
        }

        if (ffmpegErrGobler != null) {
            ffmpegErrGobler.interrupt()
            ffmpegErrGobler = null
        }

        if (ffmpegProcess != null) {
            ffmpegProcess.destroy()
            ffmpegProcess = null
        }

        super.close()
    }
}
