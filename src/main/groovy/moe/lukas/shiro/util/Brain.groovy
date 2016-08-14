package moe.lukas.shiro.util

import org.kopitubruk.util.json.JSONParser
import org.kopitubruk.util.json.JSONUtil

/**
 * Brain to store/read values of any type
 */
class Brain {
    /**
     * Whether this brain is already initialized
     */
    private static initialized = false

    /**
     * The filename for brain backups
     */
    private static filename = "brain.json"

    /**
     * The actual brain
     */
    private static LinkedHashMap storage = []

    /**
     * Init the brain from our last known backup
     */
    private static void init() {
        Scanner data = new Scanner(new File(filename))
        String json = ""

        while (data.hasNextLine()) {
            String line = data.nextLine().trim()

            if (line != "") {
                json += line
            }
        }

        data.close()
        storage = (LinkedHashMap) JSONParser.parseJSON(json)
    }

    /**
     * Backup the current brain
     */
    private static void sync() {
        PrintWriter file = new PrintWriter(filename)
        file.println(JSONUtil.toJSON(storage))
        file.close()
    }

    /**
     * Get a value
     * @param key
     * @return
     */
    static def get(String key) {
        return storage[key]
    }

    /**
     * Set a value.
     * Auto-syncs after the value was saved.
     * @param key
     * @param value
     */
    static void set(String key, def value) {
        storage[key] = value
        sync()
    }
}
