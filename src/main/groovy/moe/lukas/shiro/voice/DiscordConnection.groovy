package moe.lukas.shiro.voice

import com.eclipsesource.json.JsonValue
import groovy.transform.CompileStatic
import moe.lukas.shiro.util.HCWrapper
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

@CompileStatic
class DiscordConnection {
    /**
     * Holds an instance duh
     */
    static DiscordConnection instance

    /**
     * Discord's HTTP endpoint
     */
    private final String API_ENDPOINT = "https://discordapp.com/api"

    /**
     * Persistent WebSocket endpoint
     * (Set at runtime)
     */
    private volatile String WEBSOCKET_ENDPOINT = null

    /**
     * Bot user token
     */
    private String token = null

    /**
     * The WebSocket instance
     */
    private WebSocketClient webSocketClient

    /**
     * Singleton constructor
     * @param token
     */
    private DiscordConnection(String token) {
        this.token = token

        JsonValue gateway = HCWrapper.request(API_ENDPOINT, "gateway")
        WEBSOCKET_ENDPOINT = gateway.asObject().getString("url", null)

        webSocketClient = new WebSocketClient(new URI(WEBSOCKET_ENDPOINT)) {
            /**
             * On connection discord will send a trace and the heartbeat interval.
             * We'll start the interval in here and wait for ACK's
             *
             * @param handshakeData
             */
            @Override
            void onOpen(ServerHandshake handshakeData) {
                println("SHAKE -> " + handshakeData.httpStatusMessage)
            }

            @Override
            void onMessage(String message) {
                println("MESS -> $message")
            }

            @Override
            void onClose(int code, String reason, boolean remote) {

            }

            @Override
            void onError(Exception ex) {

            }
        }
    }

    /**
     * Instance creator
     * FUCK FACTORIES!
     *
     * @param token
     */
    static void createInstance(String token) {
        instance = new DiscordConnection(token)
    }
}
