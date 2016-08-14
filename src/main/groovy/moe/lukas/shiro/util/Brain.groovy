package moe.lukas.shiro.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Brain to store/read values of any type
 */
@Singleton(strict = false)
class Brain {
    /**
     * JsonSlurper instance to parse json
     */
    private JsonSlurper slurper

    /**
     * Constructor
     */
    private Brain() {
        this.slurper = new JsonSlurper()
        this.init()
    }

    /**
     * The filename for brain backups
     */
    private filename = "brain.json"

    /**
     * The actual brain
     */
    private LinkedHashMap storage = []

    /**
     * Init the brain from our last known backup
     */
    private void init() {
        File file = new File(this.filename)

        // Write an empty JSON file if it doesnt exist
        if (!file.exists()) {
            this.sync()
        }

        Scanner data = new Scanner(file)
        String json = ""

        while (data.hasNextLine()) {
            String line = data.nextLine().trim()

            if (line != "") {
                json += line
            }
        }

        data.close()
        this.storage = (LinkedHashMap) this.slurper.parseText(json)
    }

    /**
     * Backup the current brain
     */
    private void sync() {
        PrintWriter file = new PrintWriter(this.filename)
        file.println(JsonOutput.toJson(this.storage))
        file.close()
    }

    /**
     * Get a value
     * @param key
     * @return
     */
    def get(String key) {
        return this.storage[key]
    }

    /**
     * Set a value.
     * Auto-syncs after the value was saved.
     * @param key
     * @param value
     */
    void set(String key, def value) {
        this.storage[key] = value
        this.sync()
    }
}
