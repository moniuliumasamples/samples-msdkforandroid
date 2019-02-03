package com.moniuliuma.example.uhfreader;

import android.content.Context;
import android.media.MediaPlayer;
import java.io.PrintStream;

public class Tools
{
  public static String Bytes2HexString(byte[] b, int size)
  {
    String ret = "";
    for (int i = 0; i < size; i++)
    {
      String hex = Integer.toHexString(b[i] & 0xFF);
      if (hex.length() == 1) {
        hex = "0" + hex;
      }
      ret = ret + hex.toUpperCase();
    }
    return ret;
  }
  
  public static byte uniteBytes(byte src0, byte src1)
  {
    byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
    _b0 = (byte)(_b0 << 4);
    byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
    byte ret = (byte)(_b0 ^ _b1);
    return ret;
  }
  
  public static byte[] HexString2Bytes(String src)
  {
    int len = src.length() / 2;
    byte[] ret = new byte[len];
    byte[] tmp = src.getBytes();
    for (int i = 0; i < len; i++) {
      ret[i] = uniteBytes(tmp[(i * 2)], tmp[(i * 2 + 1)]);
    }
    return ret;
  }
  
  public static int bytesToInt(byte[] bytes)
  {
    int addr = bytes[0] & 0xFF;
    addr |= bytes[1] << 8 & 0xFF00;
    addr |= bytes[2] << 16 & 0xFF0000;
    addr |= bytes[3] << 25 & 0xFF000000;
    return addr;
  }
  
  public static byte[] intToByte(int i)
  {
    byte[] abyte0 = new byte[4];
    abyte0[0] = ((byte)(0xFF & i));
    abyte0[1] = ((byte)((0xFF00 & i) >> 8));
    abyte0[2] = ((byte)((0xFF0000 & i) >> 16));
    abyte0[3] = ((byte)((0xFF000000 & i) >> 24));
    return abyte0;
  }
  
  public static void playMedia(Context context)
  {
    System.out.println("media player");
    MediaPlayer player = MediaPlayer.create(context, 2130968576);
    if (player.isPlaying()) {
      return;
    }
    try
    {
      player.start();
    }
    catch (IllegalStateException e)
    {
      e.printStackTrace();
    }
  }
}
