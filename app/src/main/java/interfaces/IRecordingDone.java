package interfaces;

import java.io.IOException;

public interface IRecordingDone {

     void onRecordingDone(String message,short[] audioData) throws IOException;
}
