/**
 * =================================================================================================================
 * File Name            : Utils.java
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 * =====================================================================================================================
 */
package com.codegreen.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;



/**
 * @author sougata.sen
 *
 */
public class Utils {
        /**
    	 * Creation/Expiration date constants.
    	 */
    	public static int CREATION_YEAR = 0;
    	public static int CREATION_MONTH = 0;
    	public static int EXPIRATION_YEAR = 0;
    	public static int EXPIRATION_MONTH = 0;
    	
    	public static String MIME_BOUNDARY = "";
        
        /**
         * 
         * @return
         */
        public static boolean isNOTNullAndEmpty(String str){
                boolean isNotNullAndEmpty = false;
                if(str != null && str.length() > 0){
                        isNotNullAndEmpty = true;
                }
                return isNotNullAndEmpty;
        }

	/**
	 * This will replace all occourences of pattern from source string with the replacement string.
	 * @param source: Source String
	 * @param pattern: pattern to remove
	 * @param replacement: pattern to add
	 * @return
	 */
	public static String replaceAll(String source, String pattern,
			String replacement) {
		if (source == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		int idx = -1;
		int patIdx = 0;

		while ((idx = source.indexOf(pattern, patIdx)) != -1) {
			sb.append(source.substring(patIdx, idx));
			sb.append(replacement);
			patIdx = idx + pattern.length();
		}
		sb.append(source.substring(patIdx));
		return sb.toString();

	}

	/**
	 * Returns the next random number. It is used to create a password.
	 * @return
	 */
	public static String getRadomNumberInString(){
		Random random = null;
		if(random == null){
			random = new Random();
		}
		return String.valueOf(random.nextLong());
	}
	

	/**
	 * It validates the given email-ID and returns boolean value true or false.
	 * @param emailId : Email id for validation.
	 * @return
	 */
	public static boolean isValidEmail(String emailId){
		String strEmailId   =   emailId;
		int numberOfAts=0;
		int numberOfDots=0;
		int i=0;
		try {
			if(emailId.trim().equals(""))
				return false;
			if(strEmailId.length()<5){
				return false;
			}
			char[] charEmail  =   strEmailId.toCharArray();
			if(charEmail[0]== '_' || charEmail[0]== '-' || charEmail[0]=='@' || charEmail[0]== '.' || (charEmail[0]>=48 && charEmail[0]<=57)){
				return false;
			}
			while(i<strEmailId.length()){
				char c = charEmail[i];

				if(c=='@' && i<= strEmailId.length()-3 && i!=0){
					numberOfAts++;
					/**
					 * check number of @s and position of @
					 */
					if(numberOfAts > 1 || charEmail[i-1]=='.' ||charEmail[i-1]=='@'||charEmail[i-1]=='-'|| charEmail[i-1]=='_'){
						return false;
					}
				}
				int ascii   =   c;
				/**
				 * condition for checking atleast 1 dot after @
				 */
				if(c=='.' && numberOfAts==1) {
					if (i < strEmailId.length() - 1 ) {
						if (charEmail[i - 1] != '@' && charEmail[i - 1] != '.' && charEmail[i-1]!='-' &&  charEmail[i-1]!='_') {
							numberOfDots++;
						}
					} else {
						return false;
					}
				}
				/**
				 * check for a valid character
				 * A-Z 65-90,
				 * a-z 97-122
				 * 0-9 48-57,
				 * . 46, _ 95
				 * @ 64
				 */
				if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122) || (ascii >= 48 && ascii <= 57) || ascii == 95 || ascii == 46 || ascii == 64 || c == '-') {
					if (c == '-') {
						if (i != 0 && charEmail[i - 1] != '.' && charEmail[i - 1] != '@' && charEmail[i - 1] != '-' && charEmail[i - 1] != '_') {
							if (numberOfAts != 1) {
								return false;
							}
						} else {
							return false;
						}
					}
					if (c == '_') {
						if (charEmail[i - 1] == '.' || charEmail[i - 1] == '_' || charEmail[i - 1] == '-' || charEmail[i - 1] == '@') {
							return false;
						}
					}
					i++;
				} else {
					return false;
				}
			}//end of while
			if (numberOfDots < 1) {
				return false;
			}
			/**
			 * check domain name contains only letters a@b.c
			 */
			int j;
			for (j = strEmailId.length() - 1; j > 2; j--) {
				if (strEmailId.charAt(j) == '.') {
					break;
				} 
			}
			if (strEmailId.charAt(j) == '.') {
				strEmailId = strEmailId.substring(j + 1);
				for (j = 0; j < strEmailId.length(); j++) {
					if ((strEmailId.charAt(j) >= 65 && strEmailId.charAt(j) <= 90) || (strEmailId.charAt(j) >= 97 && strEmailId.charAt(j) <= 122)) {

					} else {
						return false;
					}
				}
			}
			return true;
		} catch (Exception exception) {
			//System.out.println("Exception in isValidEmail method of EmailValidate class: " + exception);
		}
		return true;
	}

	/**
	 * Converts the size into text
	 * @param size
	 * @return
	 */
	public  static final String convertSize(double size){
		String retSize = null;
		String ext = " KB";
		try {
			if(size > 0){
				size = size/1024;
				if(size >= 1024){
					size = size/1024;
					ext = " MB";
				}
				//convert to GB
				if(size >= 1024){
					size = size/1024;
					ext = " GB";
				}
				retSize = String.valueOf(size);
				//If size contains "." then use only 2 digits 
				if(retSize.indexOf(".") != -1){
					retSize = retSize.substring(0, retSize.indexOf(".") + 2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(retSize != null){
			return retSize + ext;
		}else{
			return "0.0" ;
		}
	}

	public static final String convertSize(String size){
		String retSize = null;
		String ext = " KB";
		try {
			if(size != null){


				double fileSize = Double.parseDouble(size);
				fileSize = fileSize/1024;

				//float fileSize = Float.parseFloat(size)/1024;

				if(fileSize >= 1024){
					fileSize = fileSize/1024;
					ext = " MB";
				}

				//convert to GB
				if(fileSize >= 1024){
					fileSize = fileSize/1024;
					ext = " GB";
				}
				retSize = String.valueOf(fileSize);
				//If size contains "." then use only 2 digits 
				if(retSize.indexOf(".") != -1){
					retSize = retSize.substring(0, retSize.indexOf(".") + 2);
				}



			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(retSize != null){
			return retSize + ext;
		}else{
			return "" ;
		}
	}
	/**
	 * @description : This method convert the string to string array using the delimeter symbol.
	 * @author : Ravindra Kamble
	 * @return Splitted array
	 */
	public static String[] split(String inString, String delimeter) {
		String[] retAr = null;
		Vector<String> vec = null;
		int indexA = 0;
		int indexB = 0;
		int size = 0;
		try {
			vec = new Vector<String>(1, 1);
			indexB = inString.indexOf(delimeter);
			while (indexB != -1) {
				vec.addElement(new String(inString.substring(indexA, indexB)));
				indexA = indexB + delimeter.length();
				indexB = inString.indexOf(delimeter, indexA);
			}
			vec.addElement(new String(inString.substring(indexA, inString.length())));
			size = vec.size();
			retAr = new String[size];
			for (int i = 0; i < size; i++) {
				retAr[i] = vec.elementAt(i).toString().trim();
			}

		} catch (Exception e) {
		} finally {
			vec = null;
			inString = null;
			delimeter = null;
		}
		return retAr;
	}

	/**
	 * Conevrt the date objcet into "yyyy-MM-dd'T'hh:mm:ss:aa" format.
	 * @param date
	 * @return
	 */
	public static String getCovertedDate(Date date){
		StringBuffer endDate = new StringBuffer();
		//Create the current date
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:aa");
		endDate.append(simpleDateFormat.format(date));
		endDate.delete(endDate.length() - 3, endDate.length());
		endDate.append(".00");
		return endDate.toString();
	}



	/**
	 * Convert server date format "MM/dd/yyyy HH:mm:ss:fff" into yyyy-mm-ddTHH:mm:ss.aa
	 * @param serverDate : 2010-11-25T06:17:11.9003892-07:00
	 * @return
	 */
	public static String convertServerDate(String serverDate){
		if(serverDate != null && serverDate.endsWith("Z")){
			return serverDate;
		}else{
			String[] splitedDate = split(serverDate, ".");
			StringBuffer buffer = new StringBuffer();
			try {
				if(splitedDate != null && splitedDate.length == 2){
					//add date in the format yyyy-dd-mm
					buffer.append(splitedDate[0]+ ".00Z");

					String timeDiff = splitedDate[1].substring(splitedDate[1].length() - 6, splitedDate[1].length());
					boolean add = false;
					if(timeDiff != null){
						if(timeDiff.startsWith("+")){
							add = true;
						}
					}
					timeDiff = timeDiff.substring(1, timeDiff.length());
					String[] diff = split(timeDiff, ":");

					if(diff != null && diff.length == 2){
						int hh = Integer.parseInt(diff[0]);
						int mm = Integer.parseInt(diff[1]);

						if(!add){
							hh = -hh;
							mm = -mm;
						}
					}
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
			return buffer.toString().trim();
		}
	}

	/**
	 * Returns the current date in "yyyy-MM-dd" format
	 * @return
	 */
	public static String getCurrentDate(){
		StringBuffer endDate = new StringBuffer();
		//Create the current date
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		endDate.append(simpleDateFormat.format(new Date()));

		return endDate.toString();
	}

	/**
	 * Returns the current date in "yyyy-MM-dd" format
	 * @return
	 */
	public static String getCurrentDateNTime(){
		Calendar calendar = Calendar.getInstance();

		int defaultOffset = TimeZone.getDefault().getRawOffset();

		TimeZone serverTimezone = TimeZone.getTimeZone("GMT-7");
		int serverOffset = serverTimezone.getRawOffset();
		// Get the GMT Time Zone of server("GMT-7" is the server timezone"
		calendar.set(Calendar.MILLISECOND, serverOffset - defaultOffset);

		StringBuffer endDate = new StringBuffer();


		//Create the current date
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
		endDate.append(simpleDateFormat.format(calendar.getTime()));
		endDate.append(".00Z");
		return endDate.toString();

	}

	/**
	 * Returns the current date in "yyyy-MM-dd" format
	 * @return
	 */
	public static String getLastSyncDateNTime(String dateTime){
		StringBuffer returnDate = new StringBuffer();
		try {
			if(dateTime != null){
				String[] splitedDate = split(dateTime, "T");

				returnDate.append(splitedDate[0] + "T");

				String[] time = split(splitedDate[1], ":");
				int hh = Integer.parseInt(time[0]);

				if(hh == 0){
					returnDate.append("00:00:00Z");
				}else{
					hh--;
					if(hh < 10){
						returnDate.append("0" + hh);
					}else{
						returnDate.append( hh);
					}
					returnDate.append(":");
					returnDate.append(time[1] +":");
					if(time[2].endsWith("Z")){
						returnDate.append(time[2]);
					}else{
						returnDate.append(time[2] + "Z");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnDate.toString();

	}
	/**
	 * Returns the date by removing time stamp.
	 * @param date
	 * @return
	 */
	public static String splitDate(String date){
		String retValue = getCurrentDate();
		try {
			if(date != null){
				String[] splitedDate = split(date, "T");

				retValue = splitedDate[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retValue;
	}


	/**
	 * Get l;ast day og month
	 * @param month
	 * @param year
	 * @return
	 */
	public static int getLastDayOfMonth(int month, int year){
		switch(month){
		case 2:

			// Is theYear Divisible by 4?
			if (year % 4 == 0) {

				// Is theYear Divisible by 4 but not 100?
				if (year % 100 != 0) {
					return 29;
				}
				// Is theYear Divisible by 4 and 100 and 400?
				else if (year % 400 == 0) {
					return 29;
				}
				// It is Divisible by 4 and 100 but not 400!
				else {
					return 28;
				}
			}
			// It is not divisible by 4.
			else {
				return 28;
			}

		case 4:
		case 6:
		case 9:
		case 11:
			return 30;

		default:
			return 31;
		}
	}

	public static String urlEncode(String res){
		if(res!= null){
			res = Utils.replaceAll(res, "%CC%81", "\u0301");
			res = Utils.replaceAll(res, "&", "&amp;");
			res = Utils.replaceAll(res, "\"", "&quot;");
			res = Utils.replaceAll(res, "<", "&lt;");
			res = Utils.replaceAll(res, ">", "&gt;");
			res = Utils.replaceAll(res, "%", "&#37;");
			res = Utils.replaceAll(res, "'", "&apos;");
			res = Utils.replaceAll(res, "�", "&#165;");
			res = Utils.replaceAll(res, "�", "&#163;"); 
			res = Utils.replaceAll(res, "�", "&#162;");
			res = Utils.replaceAll(res, "�", "&#191;");
			res = Utils.replaceAll(res, "�", "&#161;");
			res = Utils.replaceAll(res, "�", "&#171;");
			res = Utils.replaceAll(res, "�", "&#187;");
			res = Utils.replaceAll(res, "�", "&#169;");
			res = Utils.replaceAll(res, "�", "&#174;");
			res = Utils.replaceAll(res, "|", "&#124");
			res = Utils.replaceAll(res, "�", "&#149;");
			res = Utils.replaceAll(res, "�", "&#8220;");
			res = Utils.replaceAll(res, "�", "&#148;");
			res = Utils.replaceAll(res, "�", "&#8364;");
		}
		return res;
	}


	public static String encode(String value) {
		StringBuffer buf = new StringBuffer();
		byte[] utf;
		try {
			utf = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// this can never happen, but we might have spelled "UTF-8" wrong :)
			utf = value.getBytes(); // best fall-back strategy available
		}
		for (int i = 0; i < utf.length; ++i) {
			char b = (char) utf[i];
			if (b == ' ') buf.append('+');

			else if (isRFC3986Unreserved(b)) buf.append(b);

			else {
				buf.append('%');
				buf.append(Integer.toHexString(b).toUpperCase()); // u.c. per RFC 3986
			}
		}
		return buf.toString();
	}

	private static boolean isRFC3986Unreserved(char b) {
		return (b >= 'A' && b <= 'Z')

		|| (b >= 'a' && b <= 'z')

		|| Character.isDigit(b)

		|| ".-~_".indexOf(b) >= 0;
	}
	
	 /**
	 * Check Internet is available or not. If not then doesn't show a pop-up.
	 * @param context
	 * @return
	 */
	public  final static boolean isNetworkAvail() {
		/*ConnectivityManager connec =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connec.getActiveNetworkInfo() != null) {
			return true;
		} else {
			return false;
		}*/
		return true;
	}
	
	 /**
	 * Check if Wi-Fi available or not. If not then doesn't show a pop-up.
	 * @param context
	 * @return
	 */
	public final static boolean isWifiAvail(){
		/*WifiManager connec =  (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		ConnectivityManager connec1 =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = connec1.getNetworkInfo(1).getState();
		if(connec.isWifiEnabled() && wifi.toString().equalsIgnoreCase("CONNECTED")) {
			return true;
		} else {
			return false;
		}*/
		return true;
	}
	
	/**
	 * Converts date format from yyyy-MM-ddTHH:mm:ss to dd/mm/yyyy
	 * @param date
	 * @return
	 */
	public final static String convertDateFormat(String date){
		String finalDate = null;
		
		String[] temp = date.split("T");
		String[] newTemp = temp[0].split("-");
		finalDate = newTemp[2] + "/" + newTemp[1] + "/" + newTemp[0];
		
		return finalDate;
	}
	
	/**
	 * Returns true if SD card is present, OR else false 
	 */
	public static boolean isSdCardPresent() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	

}
