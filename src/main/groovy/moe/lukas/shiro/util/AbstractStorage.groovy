package moe.lukas.shiro.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractStorage {
    protected JsonSlurper slurper

    protected String filename = null

    protected volatile HashMap storage = [:]

    AbstractStorage(String file = "storage.json") {
        this.filename = file
        this.slurper = new JsonSlurper()
        this.init()
    }

    protected synchronized void init() {
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

    synchronized void sync() {
        PrintWriter file = new PrintWriter(this.filename)
        file.println(JsonOutput.toJson(this.storage))
        file.close()
    }

    def get(String key) {
        return this?.storage[key]
    }

    def get(String key, def fallback) {
        def o = this?.storage[key]

        if (o == null) {
            set(key, fallback)
            return fallback
        }

        return o
    }

    synchronized void set(String key, def value) {
        this.storage[key] = value
        this.sync()
    }

    synchronized void reload() {
        this.init()
    }
}
