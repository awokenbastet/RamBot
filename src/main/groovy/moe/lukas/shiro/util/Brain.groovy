package moe.lukas.shiro.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

/**
 * Brain to store/read values of any type
 */
@Singleton(strict = false)
@CompileStatic
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
    private String filename = "brain.json"

    /**
     * The actual brain
     */
    private volatile HashMap storage = [:]

    /**
     * Init the brain from our last known backup
     */
    private synchronized void init() {
        File file = new File(this.filename)

        // Write an empty JSON file if it doesn't exist
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
        this.storage = this.slurper.parseText(json) as HashMap
    }

    /**
     * Backup the current brain
     */
    public synchronized void sync() {
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
        return this?.storage[key]
    }

    /**
     * Get a value that falls back to a default if null
     * @param key
     * @param fallback
     * @return
     */
    def get(String key, def fallback) {
        def o = this?.storage[key]

        if (o == null) {
            set(key, fallback)
            return fallback
        }

        return o
    }

    /**
     * Set a value.
     * Auto-syncs after the value was saved.
     * @param key
     * @param value
     */
    synchronized void set(String key, def value) {
        this.storage[key] = value
        this.sync()
    }

    synchronized void reload() {
        this.init()
    }
}
