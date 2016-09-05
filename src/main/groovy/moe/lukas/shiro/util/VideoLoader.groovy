package moe.lukas.shiro.util

import java.util.concurrent.atomic.AtomicBoolean
import com.github.axet.vget.VGet
import com.github.axet.vget.info.VGetParser
import com.github.axet.vget.info.VideoInfo
import moe.lukas.shiro.core.Core
import org.apache.commons.io.FileUtils
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage

class VideoLoader {
    static download(String url, IChannel channel) {
        File cache = new File("cache")
        if (!cache.exists()) {
            cache.mkdir()
        }

        IMessage status = channel.sendMessage(":arrows_counterclockwise: Checking...")

        Timer.setTimeout(1000, {
            if (url.contains("vimeo") || url.contains("youtube") || url.contains("youtu.be")) {
                status.edit(":arrows_counterclockwise: Resolving...")

                Timer.setTimeout(500, {
                    String cacheName = "cache/" + Core.hash(url)
                    if (new File(cacheName).exists()) {
                        status.edit("Found! :ok_hand:")
                        return cacheName
                    }

                    URL uri = new URL(url)
                    File path = new File("cache")
                    final AtomicBoolean stop = new AtomicBoolean(false);

                    VGetParser user = VGet.parser(uri)
                    VideoInfo videoinfo = user.info(uri);
                    VGet v = new VGet(videoinfo, path);
                    v.extract(user, stop, new Runnable() {
                        @Override
                        void run() {}
                    });

                    //Create list of streams
                    String stream = null
                    videoinfo.getInfo()?.any {
                        if (it.getSource().toString().contains("mime=audio")) {
                            stream = it.getSource().toString()
                            return true
                        }
                    }

                    if (stream != null) {
                        status.edit(":arrows_counterclockwise: Downloading **${videoinfo.getTitle()}**...")

                        try {
                            FileUtils.copyURLToFile(new URL(stream), new File(cacheName), 3 * 60 * 1000, 3 * 60 * 1000)
                            status.edit("Done :ok_hand:")
                        } catch (Exception e) {
                            status.edit("Error :frowning: \n```\n" + e.getStackTrace().join("\n") + "\n```")
                        }
                    } else {
                        status.edit("Video is not downloadable :frowning:")
                    }
                })
            } else {
                status.edit(":x: This is no valid youtube/vimeo link!")
            }
        })
    }
}
