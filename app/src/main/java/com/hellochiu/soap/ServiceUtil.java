package com.hellochiu.soap;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ServiceUtil {

    private static final String NAME_SPACE = "http://tempuri.org/";
    // private static final String URL = "http://10.0.2.2:8733/SignInService/";
//    private static final String URL = "http://192.168.137.1:8733/SignInService/";
    private static final String URL = "http://192.168.0.3:8733/SignInService/";
    private static final String ACTION_PREFIX = "http://tempuri.org/IService/";
    private static final String TAG = "ServiceUtil";

    SoapObject callService(String method, String... parameters) {
        SoapObject request = new SoapObject(NAME_SPACE, method);
        for (int i = 0; i < parameters.length; i++) {
            request.addProperty(parameters[i], parameters[++i]);
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(ACTION_PREFIX + method, envelope);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        if (envelope.bodyIn instanceof SoapFault) {
            SoapFault fault = (SoapFault) envelope.bodyIn;
            Log.d(TAG, "SoapFault: " + fault.faultstring);
            return null;
        }
        return (SoapObject) envelope.bodyIn;
    }
}
