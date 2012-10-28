package network;

import java.util.*;

public class StringSanitizer
{
	public static String Sanitize(String s)
	{
		StringTokenizer st = new StringTokenizer(s, "" + (char)0);
		
		if (st.hasMoreTokens())
		{
			return st.nextToken();
		}
		else
		{
			return "";
		}
	}
}
