package com.moniuliuma.example.fingerprint;

import com.moniuliuma.fingerprint.Reader;
import com.moniuliuma.fingerprint.Reader.Priority;
import com.moniuliuma.fingerprint.DeviceException;

import com.moniuliuma.fingerprint.usbhost.DeviceUsbHost;
import com.moniuliuma.fingerprint.usbhost.UsbException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import android.content.Context;
import android.app.PendingIntent; 
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public class MainEntryActivity extends Activity
{
	private final int GENERAL_ACTIVITY_RESULT = 1;

	private static final String ACTION_USB_PERMISSION = "fingerprint.usbhost.USB_PERMISSION";
	
	private TextView m_selectedDevice;
	private Button m_getReader;
	private Button m_getCapabilities;
	private Button m_captureFingerprint;
	private Button m_streamImage;
	private Button m_enrollment;
	private Button m_verification;
	private Button m_identification;
	private String m_deviceName = "";

	Reader m_reader;

	@Override
	public void onStop()
	{
		// reset you to initial state when activity stops
		m_selectedDevice.setText("Device: (No Reader Selected)");
		setButtonsEnabled(false);
		super.onStop();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//enable tracing
		System.setProperty("DPTRACE_ON", "1");
		//System.setProperty("DPTRACE_VERBOSITY", "10");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		m_getReader = (Button) findViewById(R.id.get_reader);
		m_getCapabilities = (Button) findViewById(R.id.get_capabilities);
		m_captureFingerprint = (Button) findViewById(R.id.capture_fingerprint);
		m_streamImage = (Button) findViewById(R.id.stream_image);
		m_enrollment = (Button) findViewById(R.id.enrollment);
		m_verification = (Button) findViewById(R.id.verification);
		m_identification = (Button) findViewById(R.id.identification);
		m_selectedDevice = (TextView) findViewById(R.id.selected_device);
		m_selectedDevice.append(System.getProperty("java.vendor"));
		setButtonsEnabled(false);

		// register handler for UI elements
		m_getReader.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				launchGetReader();
			}
		});

		m_getCapabilities.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				launchGetCapabilities();
			}
		});

		m_captureFingerprint.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				launchCaptureFingerprint();
			}
		});

		m_streamImage.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				launchStreamImage();
			}
		});

		m_enrollment.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				launchEnrollment();
			}
		});

		m_verification.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				launchVerification();
			}
		});

		m_identification.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				launchIdentification();
			}
		});
	}

	protected void launchGetReader()
	{
		Intent i = new Intent(MainEntryActivity.this, GetReaderActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchGetCapabilities()
	{
		Intent i = new Intent(MainEntryActivity.this,GetCapabilitiesActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchCaptureFingerprint()
	{
		Intent i = new Intent(MainEntryActivity.this,CaptureFingerprintActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchStreamImage()
	{
		Intent i = new Intent(MainEntryActivity.this, StreamImageActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchEnrollment()
	{
		Intent i = new Intent(MainEntryActivity.this, EnrollmentActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchVerification()
	{
		Intent i = new Intent(MainEntryActivity.this, VerificationActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	protected void launchIdentification()
	{
		Intent i = new Intent(MainEntryActivity.this,IdentificationActivity.class);
		i.putExtra("device_name", m_deviceName);
		startActivityForResult(i, 1);
	}

	@Override
	public void onBackPressed()
	{
		try{
			Globals.getInstance().releaseReaders();
		}catch (Exception e) {
			e.printStackTrace();
		}
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try{
			Globals.getInstance().releaseReaders();
		}catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	protected void setButtonsEnabled(Boolean enabled)
	{
		m_getCapabilities.setEnabled(enabled);
		m_streamImage.setEnabled(enabled);
		m_captureFingerprint.setEnabled(enabled);
		m_enrollment.setEnabled(enabled);
		m_verification.setEnabled(enabled);
		m_identification.setEnabled(enabled);
	}

	protected void setButtonsEnabled_Capture(Boolean enabled)
	{
		m_captureFingerprint.setEnabled(enabled);
		m_enrollment.setEnabled(enabled);
		m_verification.setEnabled(enabled);
		m_identification.setEnabled(enabled);
	}

	protected void setButtonsEnabled_Stream(Boolean enabled)
	{
		m_streamImage.setEnabled(enabled);
	}


	protected void CheckDevice()
	{
		try
		{
			m_reader.Open(Priority.EXCLUSIVE);
			Reader.Capabilities cap = m_reader.GetCapabilities();
			setButtonsEnabled(true);
			setButtonsEnabled_Capture(cap.can_capture);
			setButtonsEnabled_Stream(cap.can_stream);
			m_reader.Close();
		} 
		catch (DeviceException e1)
		{
			displayReaderNotFound();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data == null)
		{
			displayReaderNotFound();
			return;
		}
		Log.d("USBHost", "onActivityResult " + requestCode);
		Globals.ClearLastBitmap();
		m_deviceName = (String) data.getExtras().get("device_name");

		switch (requestCode)
		{
		case GENERAL_ACTIVITY_RESULT:

			if((m_deviceName != null) && !m_deviceName.isEmpty())
			{
				m_selectedDevice.setText("Device: " + m_deviceName);

				try {
					Context applContext = getApplicationContext();
					m_reader = Globals.getInstance().getReader(m_deviceName, applContext);

					{
						PendingIntent mPermissionIntent;
						mPermissionIntent = PendingIntent.getBroadcast(applContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
						IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
						applContext.registerReceiver(mUsbReceiver, filter);

						if(DeviceUsbHost.UsbCheckAndRequestPermissions(applContext, mPermissionIntent, m_deviceName))
						{
							CheckDevice();
						}
					}
				} catch (DeviceException e1)
				{
					displayReaderNotFound();
				}
				catch (UsbException e)
				{
					displayReaderNotFound();
				}

			} else
			{ 
				displayReaderNotFound();
			}

			break;
		}
	}

	private void displayReaderNotFound()
	{
		m_selectedDevice.setText("Device: (No Reader Selected)");
		setButtonsEnabled(false);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Reader Not Found");
		alertDialogBuilder.setMessage("Plug in a reader and try again.").setCancelable(false).setPositiveButton("Ok",
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog,int id) {}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action))
			{
				synchronized (this)
				{
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
					{
						if(device != null)
						{
							//call method to set up device communication
							CheckDevice();
						}
					}
	    			else
	    			{
	    				setButtonsEnabled(false);
	    			}
	    		}
	    	}
	    }
	};
}
