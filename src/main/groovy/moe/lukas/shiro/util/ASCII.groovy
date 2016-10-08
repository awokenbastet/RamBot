package moe.lukas.shiro.util

/**
 * Helper for ASCII-Art related stuff
 */
class ASCII {
    /**
     * Draws an ASCII table
     * Full credit to @Kaaz who gracefully donated this piece of code to shiro.
     *
     * @link https://github.com/MaikWezinkhof/DiscordBot/blob/master/src/main/java/discordbot/util/Misc.java#L131
     * @param headers Table headers
     * @param rows Array of rows
     * @return
     */
    static String drawTable(List<String> headers, List<List<String>> rows) {
        String sb = ""
        int padding = 1
        int[] widths = new int[headers.size()]

        (0..widths.length - 1).each { int i -> widths[i] = 0 }

        (0..headers.size() - 1).each { int i ->
            if (headers[i].length() > widths[i]) {
                widths[i] = headers[i].length()
            }
        }

        rows.each {
            (0..it.size() - 1).each { int i ->
                String cell = it[i]
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length()
                }
            }
        }

        sb += "```xl\n"

        String formatLine = "┃"
        widths.each { formatLine += " %-" + it + "s ┃" }
        formatLine += "\n"

        sb += appendSeparatorLine("┏", "┳", "┓", padding, widths)
        sb += String.format(formatLine, headers.toArray())
        sb += appendSeparatorLine("┣", "╋", "┫", padding, widths)

        rows.each { sb += String.format(formatLine, it.toArray()) }

        sb += appendSeparatorLine("┗", "┻", "┛", padding, widths)
        sb += "```"

        return sb
    }

    /**
     * Mini-helper to keep ASCII#drawTable() clean
     *
     * @param left
     * @param middle
     * @param right
     * @param padding
     * @param sizes
     * @return
     */
    private static String appendSeparatorLine(String left, String middle, String right, int padding, int ... sizes) {
        boolean first = true;
        String ret = ""

        sizes.each {
            if (first) {
                first = false;
                ret += left + ("━" * (it + padding * 2))
            } else {
                ret += middle + ("━" * (it + padding * 2))
            }
        }

        return ret += right + "\n"
    }
}
