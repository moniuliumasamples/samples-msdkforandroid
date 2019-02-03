package com.moniuliuma.example.cardreader.util;

public class General {
	public static void strcpy(byte[] des, byte[] source,int start)
	{
		for(int i =0;i<source.length&&(i+start)<des.length;i++)
		{
			des[i+start] = source[i];
		}
	}
	public static void strcpy(char[] des,int start, byte[] source)
	{
		for(int i =0;i<source.length&&(i+start)<des.length;i++)
		{
			des[i+start] = (char) source[i];
		}
	}
	public static void strcpy(byte[] des, byte[] source)
	{
		for(int i =0;i<source.length&&i<des.length;i++)
		{
			des[i] = source[i];
		}
	}
	public static void strcpy(byte[] des, String source)
	{
		byte[] temp = source.getBytes();
		for(int i =0;i<temp.length&&i<des.length;i++)
		{
			des[i] = temp[i];
		}
	}
	public static void strcpy(char[] des, String source)
	{
		for(int i =0;i<des.length&&i<source.length();i++)
		{
			des[i] = source.charAt(i);
		}
	}
	public static void strcpy(char[] des,int start, String source)
	{
		for(int i =0;i<source.length()&&(i+start)<des.length;i++)
		{
			des[i+start] = source.charAt(i);
		}
	}
	public static void strcpy(char[] des, char[] source)
	{
		strcpy(des,0,source,0);
	}
	public static void strcpy(char[] des, int dStart, char[] source,int sStart)
	{
		for(int i =0;(i+sStart)<source.length&&(i+dStart)<des.length;i++)
		{
			des[i+dStart] = source[i+sStart];
		}
	}
	
	
	public static void DatBcdToAsc(byte[] Asc, byte[] Bcd, int Asc_len)
	{
		DatBcdToAsc( Asc, 0, Bcd, 0, Asc_len);
	}
	public static void DatBcdToAsc(byte[] Asc, int aStart, byte[] Bcd,int Asc_len)
	{
		DatBcdToAsc( Asc, aStart, Bcd, 0, Asc_len);
	}
	public static void DatBcdToAsc(byte[] Asc, byte[] Bcd, int bStart, int Asc_len)
	{
		DatBcdToAsc( Asc, 0, Bcd, bStart, Asc_len);
	}
	public static void DatBcdToAsc(byte[] Asc, int aStart, byte[] Bcd, int bStart,int Asc_len)
	{
		/*~~~~~~~~~~~~~*/
		byte	is_first;
		byte	by;
		/*~~~~~~~~~~~~~*/

		is_first = (byte) (Asc_len % 2);				/* change by wxk 98.11.06 */

		int i = bStart;
		int j = aStart;
		while(Asc_len-- > 0)
		{
			if(is_first!=0)
			{
				by = (byte) (Bcd[i] & 0x0f);
				i++;
			}
			else
			{
				by = (byte) ((Bcd[i] >> 4) & 0x0f);
			}

			by += (by >= 0x0a) ? 0x37 : '0';	/* 0x37 = 'A' - 0x0a */

			Asc[j++] = by;
			if(is_first == 0)
			{
				is_first = 1;
			}
			else
			{
				is_first = 0;
			}
		}
	}
	
	public static void DatAscToBcd(byte[] Bcd, byte[] Asc, int Asc_len)
	{
		DatAscToBcd(Bcd, 0, Asc, 0,Asc_len);
	}
	public static void DatAscToBcd(byte[] Bcd,int bStart, byte[] Asc,int aStart, int Asc_len)
	{
		/*~~~~~~~~~~~~~~~~*/
		boolean	is_high; 
		byte by;
		/*~~~~~~~~~~~~~~~~*/

		is_high = (Asc_len % 2) == 0;
		Bcd[bStart] = 0x00;
		int i = bStart;
		int j = aStart;
		while(Asc_len-- > 0)
		{
			by = Asc[j++];

			if((by & 0x10) == 0 && (by > 0x30))
			{
				by += 9;
			}
			/* ����ĸ�Ϳո�Ĵ���,Сд���д,�ո��0 */
			if(is_high)
			{
				Bcd[i] = (byte) (by  << 4);
			}
			else
			{
				by &= 0x0f;
				Bcd[i++] |= by;
			}

			is_high = !is_high;
		}
	}
	
	
	public static int memcmp(byte[] buf1, byte[] buf2, int Asc_len)
	{
		return memcmp(buf1,0,buf2,0,Asc_len);
	}
	public static int memcmp(byte[] buf1,int start, byte[] buf2, int Asc_len)
	{
		return memcmp(buf1,start,buf2,0,Asc_len);
	}

	public static int memcmp(byte[] buf1, byte[] buf2,int start, int Asc_len)
	{
		return memcmp(buf1,0,buf2,start,Asc_len);
	}
	public static int memcmp(byte[] buf1,int start1, byte[] buf2,int start2, int Asc_len)
	{
		for(int i =0;i<Asc_len&&i<(buf1.length-start1)&&i<(buf2.length-start2);i++)
		{
			if(buf1[i+start1]<buf2[i+start2])
			{
				return -1;
			}
			else if(buf1[i+start1]>buf2[i+start2])
			{
				return 1;
			}
			else
			{
			}
		}
		return 0;
	}
	public static int memcmp(char[] buf1, char[] buf2, int Asc_len)
	{
		return memcmp(buf1,0,buf2,0,Asc_len);
	}
	public static int memcmp(char[] buf1,int start, char[] buf2, int Asc_len)
	{
		return memcmp(buf1,start,buf2,0,Asc_len);
	}

	public static int memcmp(char[] buf1, char[] buf2,int start, int Asc_len)
	{
		return memcmp(buf1,0,buf2,start,Asc_len);
	}
	public static int memcmp(char[] buf1,int start1, char[] buf2,int start2, int Asc_len)
	{
		for(int i =0;i<Asc_len&&i<(buf1.length-start1)&&i<(buf2.length-start2);i++)
		{
			if(buf1[i+start1]<buf2[i+start2])
			{
				return -1;
			}
			else if(buf1[i+start1]>buf2[i+start2])
			{
				return 1;
			}
			else
			{
			}
		}
		return 0;
	}
	public static int memcmp(String hexString, byte[] buf2, int Asc_len)
	{
		int hexStringLen = hexString.length();
		if(hexStringLen == Asc_len)
		{
			byte[] buf1 = hexString.getBytes();
			for(int i =0;i<Asc_len;i++)
			{
				if(buf1[i]<buf2[i])
				{
					return -1;
				}
				else if(buf1[i]>buf2[i])
				{
					return 1;
				}
			}
		}
		else
		{
		    char[] achar = hexString.toLowerCase().toCharArray(); 
		    byte[] buf1 = new byte[Asc_len];
		    for (int i = 0; i < Asc_len; i++) { 
		     int pos = i * 2; 
		     buf1[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1])); 
		    }
		    for(int i =0;i<Asc_len;i++)
			{
				if(buf1[i]<buf2[i])
				{
					return -1;
				}
				else if(buf1[i]>buf2[i])
				{
					return 1;
				}
			}
		}
		return 0;
	}
	public static int memcmp(byte[] buf1, String hexString, int Asc_len)
	{
		return memcmp(buf1,0,hexString,Asc_len);
	}
	public static int memcmp(byte[] buf1,int start, String hexString, int Asc_len)
	{
		int hexStringLen = hexString.length();
		if(hexStringLen == Asc_len)
		{
			byte[] buf2 = hexString.getBytes();
			for(int i =0;i<Asc_len&&i<(buf1.length-start)&&i<hexStringLen;i++)
			{
				if(buf1[i+start]<buf2[i])
				{
					return -1;
				}
				else if(buf1[i+start]>buf2[i])
				{
					return 1;
				}
			}
		}
		else
		{
		    char[] achar = hexString.toLowerCase().toCharArray(); 
		    byte[] buf2 = new byte[Asc_len];
		    for (int i = 0; i < Asc_len&&i<(buf1.length-start)&&(i*2+1)<hexStringLen; i++) { 
		    	int pos = i * 2; 
		    	buf2[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1])); 
		    }
		    for(int i =0;i<Asc_len&&i<(buf1.length-start);i++)
			{
				if(buf1[i+start]<buf2[i])
				{
					return -1;
				}
				else if(buf1[i+start]>buf2[i])
				{
					return 1;
				}
			}
		}
		return 0;
	}
	
	private static byte toByte(char c) { 
	    byte b = (byte) "0123456789abcdef".indexOf(c); 
	    return b; 
	}
	
	
	public static void memcpy(byte[] des, byte[] source,int len)
	{
		memcpy(des,0,source,0,len);
	}
	public static void memcpy(byte[] des,int start, byte[] source,int len)
	{
		memcpy(des,start,source,0,len);
	}

	public static void memcpy(byte[] des, byte[] source,int start,int len)
	{
		memcpy(des,0,source,start,len);
	}
	public static void memcpy(byte[] des,int dstart, byte[] source,int sstart,int len)
	{
		int i;
    	for(i=0;(i<len)&&i<(des.length-dstart)&&i<(source.length-sstart);i++)
    		des[i+dstart]=source[i+sstart];
	}
	
	public static void memcpy(char[] des, char[] source,int len)
	{
		memcpy(des,0,source,0,len);
	}
	public static void memcpy(char[] des,int start, char[] source,int len)
	{
		memcpy(des,start,source,0,len);
	}

	public static void memcpy(char[] des, char[] source,int start,int len)
	{
		memcpy(des,0,source,start,len);
	}
	public static void memcpy(char[] des,int dstart, char[] source,int sstart,int len)
	{
		int i;
    	for(i=0;(i<len)&&i<(des.length-dstart)&&i<(source.length-sstart);i++)
    		des[i+dstart]=source[i+sstart];
	}
	
	public static void memcpy(char[] des, byte[] source,int len)
	{
		memcpy(des,0,source,0,len);
	}
	public static void memcpy(char[] des,int start, byte[] source,int len)
	{
		memcpy(des,start,source,0,len);
	}

	public static void memcpy(char[] des, byte[] source,int start,int len)
	{
		memcpy(des,0,source,start,len);
	}
	public static void memcpy(char[] des,int dstart, byte[] source,int sstart,int len)
	{
		int i;
    	for(i=0;(i<len)&&i<(des.length-dstart)&&i<(source.length-sstart);i++)
    		des[i+dstart]=(char) source[i+sstart];
	}


	public static void memcpy(char[] des, String source)
	{
		char[] sour = source.toCharArray();
		
		int i;
    	for(i=0;i<source.length()&&i<des.length;i++)des[i]=sour[i];
	}

	public static void memcpy(byte[] des, String hexString,int len)
	{
		memcpy(des,0,hexString,len);
	}
	public static void memcpy(byte[] des,int start, String hexString,int len)
	{
		if(len != hexString.length())
		{
			char[] achar = hexString.toLowerCase().toCharArray(); 
		    for (int i = 0; i < len&&i<(des.length-start)&&(i*2+1)<achar.length; i++) { 
			     int pos = i * 2; 
			     des[start+i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1])); 
			} 
		}
		else
		{
			byte[] source = hexString.getBytes();
			for(int i=0;i<len&&i<(des.length-start)&&i<source.length;i++)des[start+i]=source[i];
		}
	}
	
}
