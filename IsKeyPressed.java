import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class IsKeyPressed {
    private static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
    public static boolean isPressed(int key) {
        synchronized (IsKeyPressed.class) {
            return keys.containsKey(key) && keys.get(key);
        }
    }

    public static void init() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (IsKeyPressed.class) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_PRESSED:
                        keys.put(ke.getKeyCode(), true);
                        break;
                    case KeyEvent.KEY_RELEASED:
                        keys.put(ke.getKeyCode(), false);
                        break;
                    }
                    return false;
                }
            }
        });
    }
}