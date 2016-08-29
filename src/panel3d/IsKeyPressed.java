package panel3d;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.ArrayList;

public class IsKeyPressed {
    private static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
    private static HashMap<Integer, ArrayList<KeyEventListener>> events = new HashMap<Integer, ArrayList<KeyEventListener>>();
    
    public static boolean isPressed(int key) {
        synchronized (IsKeyPressed.class) {
            return keys.containsKey(key) && keys.get(key);
        }
    }
    
    public static void addListener(Integer key, KeyEventListener listener)
    {
        if(!events.containsKey(key))
            events.put(key, new ArrayList<KeyEventListener>());        
        events.get(key).add(listener);
    }
    
    public static void removeListener(Integer key, KeyEventListener listener)
    {
        if(!events.containsKey(key))
            return;    
        events.get(key).remove(listener);
    }

    public static void init() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (IsKeyPressed.class) {
                    ArrayList<KeyEventListener> keyEvents = events.get(ke.getKeyCode());
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            keys.put(ke.getKeyCode(), true);
                            if(keyEvents != null)
                                for(KeyEventListener k : keyEvents)
                                    k.OnKey(true);
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keys.put(ke.getKeyCode(), false);
                            if(keyEvents != null)
                                for(KeyEventListener k : keyEvents)
                                    k.OnKey(false);
                            break;
                    }
                    return false;
                }
            }
        });
    }
    
}