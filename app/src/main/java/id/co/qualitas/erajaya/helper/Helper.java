package id.co.qualitas.erajaya.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import id.co.qualitas.erajaya.R;
import id.co.qualitas.erajaya.activity.LoginActivity;
import id.co.qualitas.erajaya.constants.Constants;
import id.co.qualitas.erajaya.interfaces.MultipleCallback;
import id.co.qualitas.erajaya.session.SessionManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Helper {
	
	private static Boolean requestUrl = false;
    private static Map<String, Object> param = new HashMap<String, Object>();
    private static Map<String, Object> cart = new HashMap<String, Object>();
    public static Map<String, Object> getParam() {
        return param;
    }
    public static int material_number = 0;
    

    public static Boolean getRequestUrl() {
		return requestUrl;
	}



	public static void setRequestUrl(Boolean requestUrl) {
		Helper.requestUrl = requestUrl;
	}



	public static Object getItemParam(String key) {
        return param.get(key);
    }

    public static void setItemParam(String key, Object object) {
        param.put(key, object);
    }

    public static void removeItemParam(String key) {
        param.remove(key);
    }

    public static BottomSheetDialog showBottomSheetDialog(Activity activity, int resId, MultipleCallback<View, BottomSheetDialog> initView) {
        View view = LayoutInflater.from(activity).inflate(resId, activity.findViewById(android.R.id.content), false);
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.setOnShowListener(dialog1 -> {
            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet == null)
                return;
            bottomSheet.setBackgroundResource(0);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        dialog.setContentView(view);
        initView.onCallback(view, dialog);
        dialog.show();
        return dialog;
    }

    public static String intToString(int num, int digits) {
        assert digits > 0 : "Invalid number of digits";

        // create variable length array of zeros
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

        return df.format(num);
    }

//    public static String imageLoader(String url, String id) {
//        String token = Helper.getItemParam(Constants.TOKEN).toString();
//        String urls = null;
//        try {
//            urls = Helper.getItemParam(Constants.URL).toString() + url +
//                    id + "?token=" + URLEncoder.encode(token, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return urls;
//    }

    public static String getApplicationName(Context context) {
        return String.valueOf(context.getApplicationInfo().loadLabel(context.getPackageManager()));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Object postWebserviceLogin(String url, Class<?> responseType, Object body) {
        //kalo post, kita kasih class k back end
        int flag = 0;
        ResponseEntity<?> responseEntity = null;
        while (flag == 0) {
            flag = 1;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

            requestHeaders.set("Authorization", Constants.AUTHORIZATION_LOGIN);
            requestHeaders.set("Content-Type", Constants.HTTP_HEADER_CONTENT_TYPE);

            HttpEntity<?> requestEntity = new HttpEntity<Object>(body, requestHeaders);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
            } catch (Exception e) {
                if (e.getMessage().contains("400")) {
                    Helper.setItemParam(Constants.ERROR_LOGIN, "1");
                }
                if (e.getMessage().equals("401 Unauthorized")) ;
            }

        }
        return responseEntity.getBody();
    }

    public static boolean isEmpty(Object obj) {
        boolean bool = false;
        if (obj instanceof EditText) {
            if (TextUtils.isEmpty(((EditText) obj).getText()))
                bool = true;
        }
        return bool;
    }

    public static boolean isEquals(Object obj1, Object obj2) {
        CharSequence charSequence1 = null;
        CharSequence charSequence2 = null;
        boolean bool = false;

        if (obj1 instanceof EditText) {
            charSequence1 = ((EditText) obj1).getText();
        }

        if (obj2 instanceof EditText) {
            charSequence2 = ((EditText) obj2).getText();
        }

        if (TextUtils.equals(charSequence1, charSequence2))
            bool = true;
        return bool;
    }

    public static String todayDate1(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getDateFromMilliSec(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static Calendar todayDate() {
        Calendar c = Calendar.getInstance();
        return c;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidPhone(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.PHONE.matcher(target).matches();
    }


    public final static boolean isValidAddress(String address) {
        boolean check;
        if (address.length() < 3) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }


    public static Object postWebservice(String url, Object request,
                                        Class<?> responseType) {
        //kita memberikan sesuatu k back end, dan kan menrima balikannya
        RestTemplate restTemplate = new RestTemplate();
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
        return restTemplate.postForObject(url, request, responseType);
    }

    public static Object getWebservice(String url, Class<?> responseType) {
        int flag = 0;

        HttpEntity<?> response = null;
//        while (flag == 0) {
        flag = 1;
        //kalo get,hanya menerima data dari back end
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();
        String token = (String) Helper.getItemParam(Constants.TOKEN);
        String bearerToken = Constants.BEARER.concat(token.toString());
        requestHeaders.set("Authorization", bearerToken);

        HttpEntity<?> entity = new HttpEntity<Object>(requestHeaders);
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        } catch (Exception e) {
            if (e.getMessage().equals("401 Unauthorized"))
                flag = 0;
        }
//        }
        return response.getBody();
    }

    public static Object postWebserviceWithBody(String url, Class<?> responseType, Object body, Context context) {
        //kalo post, kita kasih class k back end
        int flag = 0;
        ResponseEntity<?> responseEntity = null;
        while (flag == 0) {
            flag = 1;

            String token = (String) Helper.getItemParam(Constants.TOKEN);
            String bearerToken = Constants.BEARER.concat(token);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            requestHeaders.set("Authorization", bearerToken);
            HttpEntity<?> requestEntity = new HttpEntity<>(body, requestHeaders);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
            } catch (Exception e) {
                if (e.getMessage().equals("401 Unauthorized")) {
//                    flag = 0;
                    Helper.removeItemParam(Constants.LIST_WH);
                    new SessionManager(context).clearData();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                }
            }
        }
        return responseEntity.getBody();
    }


    public static Object stringToObject(String str) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(str.getBytes()), Base64.NO_PADDING
                    | Base64.NO_WRAP)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String convertHmsTimer(Long millis) {
		return String.format(
				Locale.getDefault(),
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						% TimeUnit.MINUTES.toSeconds(1));
	}

//    public static Menu setMenu() {
//        LoginResponse loginResponse = (LoginResponse) Helper.getItemParam(Constants.LOGIN);
//        //ambil data role user utk validasi menu
//        List<MenuUser> menu = loginResponse.getMenu();
//        Menu menuQita = new Menu();
//        for (int i = 0; i < menu.size(); i++) {
//            if (menu.get(i).getIdMenu() == 1)
//                menuQita.setMenuDayoff(true);
//            else if (menu.get(i).getIdMenu() == 2)
//                menuQita.setMenuTimesheet(true);
//            else if (menu.get(i).getIdMenu() == 3)
//                menuQita.setMenuClaim(true);
//            else if (menu.get(i).getIdMenu() == 4)
//                menuQita.setMenuCalendar(true);
//            else if (menu.get(i).getIdMenu() == 5)
//                menuQita.setMenuApprovalDayOff(true);
//            else if (menu.get(i).getIdMenu() == 6)
//                menuQita.setMenuApprovalTimesheet(true);
//            else if (menu.get(i).getIdMenu() == 7)
//                menuQita.setMenuApprovalClaim(true);
//            else if (menu.get(i).getIdMenu() == 67)
//                menuQita.setMenuGoodReceipt(true);
//            else if (menu.get(i).getIdMenu() == 66)//back end belum kasih id menunya
//                menuQita.setMenuTravelRequest(true);
//        }
//        return menuQita;
//    }
//
//
//    public static Object getWebserviceWithoutHeaders(String url, Class<?> responseType) {
//        //kalo get,hanya menerima data dari back end
//        RestTemplate restTemplate = new RestTemplate();
//        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
//        restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
//        return restTemplate.getForObject(url, responseType);
//    }
//
//    //USE
//    public static Object getWebservice(String url, Class<?> responseType) {
//        int flag = 0;
//        HttpEntity<?> response = null;
//        while (flag == 0) {
//            flag = 1;
//            //kalo get,hanya menerima data dari back end
//            RestTemplate restTemplate = new RestTemplate();
//
//            HttpHeaders requestHeaders = new HttpHeaders();
//            String token = (String) Helper.getItemParam(Constants.TOKEN);
//            requestHeaders.set("X-Token", token);
//            HttpEntity<?> entity = new HttpEntity<Object>(requestHeaders);
//            GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
//            restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
//            try {
//                response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
//            } catch (Exception e) {
//                if (e.getMessage().equals("401 Unauthorized"))
//                    flag = 0;
//            }
//
//            Object xtoken;
//
//            if (flag == 0) {
//                ResponseEntity<?> responseEntity = null;
//                String urls = Helper.getItemParam(Constants.URL).toString() + Constants.URL_REFRESH_TOKEN;
//                LoginResponse loginResponse = (LoginResponse) Helper.getItemParam(Constants.LOGIN);
//                String idClient = loginResponse.getClientId();
//                String id = loginResponse.getToken();
//                RefreshTokenRequest requestToken = new RefreshTokenRequest();
//                requestToken.setId(id);
//                requestToken.setIdClient(idClient);
//                MessageResponse message = new MessageResponse();
//                responseEntity = postWebserviceWithBodyWithoutHeaders(urls, MessageResponse.class, requestToken);
//                message = (MessageResponse) responseEntity.getBody();
//                xtoken = message.getResult();
//                if (xtoken != null) {
//                    Helper.setItemParam(Constants.FLAG_TOKEN, "1");
//                    Helper.setItemParam(Constants.TOKEN, xtoken.toString());
//                }
//            }
//        }
//        return response.getBody();
//    }
//
//
//    //USE
//    public static Object postWebserviceWithBody(String url, Class<?> responseType, Object body) {
//        //kalo post, kita kasih class k back end
//        int flag = 0;
//        ResponseEntity<?> responseEntity = null;
//        while (flag == 0) {
//            flag = 1;
//            String registerId = (String) Helper.getItemParam(Constants.REGIISTERID);
//            String token = (String) Helper.getItemParam(Constants.TOKEN);
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders requestHeaders = new HttpHeaders();
//            requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//            requestHeaders.set("X-Token", token);
//            //        requestHeaders.set("Device-ID",registerId);
//            HttpEntity<?> requestEntity = new HttpEntity<Object>(body, requestHeaders);
//            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
//
//            try {
//                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
//            } catch (Exception e) {
//                if (e.getMessage().equals("401 Unauthorized"))
//                    flag = 0;
//            }
//
//            Object xtoken;
//
//            if (flag == 0) {
//                String urls = Helper.getItemParam(Constants.URL).toString() + Constants.URL_REFRESH_TOKEN;
//                LoginResponse loginResponse = (LoginResponse) Helper.getItemParam(Constants.LOGIN);
//                String idClient = loginResponse.getClientId();
//                String id = loginResponse.getToken();
//                RefreshTokenRequest requestToken = new RefreshTokenRequest();
//                requestToken.setId(id);
//                requestToken.setIdClient(idClient);
//                MessageResponse message = new MessageResponse();
//                responseEntity = postWebserviceWithBodyWithoutHeaders(urls, MessageResponse.class, requestToken);
//                message = (MessageResponse) responseEntity.getBody();
//                xtoken = message.getResult();
//                if (xtoken != null) {
//                    Helper.setItemParam(Constants.FLAG_TOKEN, "1");
//                    Helper.setItemParam(Constants.TOKEN, xtoken.toString());
//                }
//            }
//        }
//        return responseEntity.getBody();
//    }
//
//    //USE
//    public static ResponseEntity<?> postWebserviceWithBodyWithoutHeaders(String url, Class<?> responseType, Object body) {
//        //kalo post, kita kasih class k back end
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//        HttpEntity<?> requestEntity = new HttpEntity<Object>(body, requestHeaders);
//        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
//        ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
//        return responseEntity;
//    }

    public static Bitmap decodeSampleImage(File f, int width, int height) {
        try {
            System.gc(); // First of all free some memory

            // Decode image size

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to

            final int requiredWidth = width;
            final int requiredHeight = height;

            // Find the scale value (as a power of 2)

            int sampleScaleSize = 1;

            while (o.outWidth / sampleScaleSize / 2 >= requiredWidth && o.outHeight / sampleScaleSize / 2 >= requiredHeight)
                sampleScaleSize *= 2;

            // Decode with inSampleSize

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = sampleScaleSize;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
            Log.v("", e.getMessage()); // We don't want the application to just throw an exception
        }

        return null;
    }

    public static String objectToString(Serializable obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(
                    new Base64OutputStream(baos, Base64.NO_PADDING
                            | Base64.NO_WRAP));
            oos.writeObject(obj);
            oos.close();
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


//    public static void triggerNotif(Context context, GCMResponseDemo gcmResponseDemo) {
//        Intent intent = new Intent(context, NotificationActivityDemo.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.logo_qlt)
//                .setContentTitle("Internal Project")
//                .setContentText(gcmResponseDemo.getMessage())
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setTicker(gcmResponseDemo.getTickerText())//tulisan yang ada di paling atas (yang jalan)
//                .setContentIntent(pendingIntent);
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//    }


    public static String changeDateFormat(String temp) {
        Date dateTimesheet = null;
        try {
            dateTimesheet = new SimpleDateFormat("yyyy-MM-dd").parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateTimesheetString2 = new SimpleDateFormat("dd MMMM yyyy").format(dateTimesheet);
        return dateTimesheetString2;
    }

    public static String changeDateFormatReverse(String temp) {
        Date dateTimesheet = null;
        try {
            dateTimesheet = new SimpleDateFormat("dd MMMM yyyy").parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateTimesheetString2 = new SimpleDateFormat("yyyy-MM-dd").format(dateTimesheet);
        return dateTimesheetString2;
    }

    public static Date convertStringtoDate(String temp) {
        Date dateTimesheet = null;
        try {
            dateTimesheet = new SimpleDateFormat("yyyy-MM-dd").parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimesheet;
    }

    public static Date convertStringtoDate2(String temp) {
        Date dateTimesheet = null;
        try {
            dateTimesheet = new SimpleDateFormat("dd MMMM yyyy").parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimesheet;
    }

    public static String splitAndChangeDateFormat(String string, String from, String to) {
        String temp[] = string.split("T");
        String result = changeDateFomat(from, to, temp[0]);
        return result;
    }


//    public static String splitTime(String string) {
//        String temp[] = string.split("T");
//        String result = changeDateFomat(Constants.DATE_TYPE_6, Constants.DATE_TYPE_9, temp[1]);
//        return result;
//    }
//
//    public static Date convertStringtoDateNew(String format, String date) {
//        Date dateTimesheet = null;
//        try {
//            dateTimesheet = new SimpleDateFormat(format).parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return dateTimesheet;
//    }
//
//    public static DateChange setCalendar(EditText edtMeetingDate) {
//        String timeTo = Helper.changeDateFomat(Constants.ddMMMMyyyy, Constants.yyyyMMdd, edtMeetingDate.getText().toString());
//
//        String temp2[] = timeTo.split("-");
//        int yearMeetingDate = Integer.parseInt(temp2[0]);
//        int monthOfYearMeetingDate = Integer.parseInt(temp2[1]) - 1;
//        int dayOfMonthMeetingDate = Integer.parseInt(temp2[2]);
//        DateChange dateChange = new DateChange(dayOfMonthMeetingDate, monthOfYearMeetingDate, yearMeetingDate);
//        return dateChange;
//    }

    public static String changeDateFomat(String from, String to, String temp) {
        Date date = null;
        try {
            date = new SimpleDateFormat(from).parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat(to).format(date);
    }

    public static Date changeFormatDatetoDate(String formatFrom, String formatTo, String date) {
        Date dateDate = null;
        try {
            dateDate = new SimpleDateFormat(formatFrom).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateDate;
    }

    public static String changeFormatDate(String formatFrom, String formatTo, String date) {
        Date dateDate = null;
        try {
            dateDate = new SimpleDateFormat(formatFrom).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateString = new SimpleDateFormat(formatTo).format(dateDate);

        return dateString;
    }

    public static String convertDateToStringNew(String format, Date temp) {
        String dateTimesheetString2 = new SimpleDateFormat(format).format(temp);
        return dateTimesheetString2;
    }

    public static String convertDateToSting(Date temp) {
        String dateTimesheetString2 = new SimpleDateFormat("yyyy-MM-dd").format(temp);
        return dateTimesheetString2;
    }

    public static String convertDateToSting2(Date temp) {
        String dateTimesheetString2 = new SimpleDateFormat("dd MMMM yyyy").format(temp);
        return dateTimesheetString2;
    }

    public static String toRupiahFormat(String nominal) {
        String rupiah = "";
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat
                .getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        kursIndonesia.setMaximumFractionDigits(0);
        kursIndonesia.setMinimumFractionDigits(0);
        formatRp.setCurrencySymbol("");
        // formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        rupiah = kursIndonesia.format(Double.parseDouble(nominal));

        return rupiah;
    }

    public static String removeFromDouble(double price) {
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(price);
    }

    public static String toRupiahFormatWithoutDot(String nominal) {
        String rupiah = "";
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat
                .getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        kursIndonesia.setMaximumFractionDigits(0);
        kursIndonesia.setMinimumFractionDigits(0);
        formatRp.setCurrencySymbol("");
        // formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        rupiah = kursIndonesia.format(Double.parseDouble(nominal));

        return rupiah;
    }

    public static String toNoRupiahFormat(String nominal) {
        String rupiah = nominal.replace(".", "");
        return rupiah;
    }

    public static String splitDateandConvert(String dateTemp, String formatFrom, String formatTo) {
        String result[] = dateTemp.split("T");
        String dateString = result[0];
        String dateString2 = "";
        Date dateDate = null;
        try {
            dateDate = new SimpleDateFormat(formatFrom).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateString2 = new SimpleDateFormat(formatTo).format(dateDate);
        return dateString2;
    }

    public static Date splitDateandConverttoDate(String dateTemp, String formatFrom) {
        String result[] = dateTemp.split("T");
        String dateString = result[0];
        Date dateDate = null;
        try {
            dateDate = new SimpleDateFormat(formatFrom).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateDate;
    }

    public static String splitDateandConvertSpace(String dateTemp, String formatFrom, String formatTo) {
        String result[] = dateTemp.split(" ");
        String dateString = result[0];
        String dateString2 = "";
        Date dateDate = null;
        try {
            dateDate = new SimpleDateFormat(formatFrom).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateString2 = new SimpleDateFormat(formatTo).format(dateDate);
        return dateString2;
    }

//    public static String splitAndGetTime(String time) {
//        String temp2[] = time.split(Constants.T);
//        String timeToString = temp2[1].substring(0, 5);
//        return timeToString;
//    }

    public static String splitAmount(String amount) {
        String temp2[] = amount.split(".");
        String timeToString = temp2[0];
        return timeToString;
    }

    public static String splitAndGetTimeMeeting(String time) {
        String temp2[] = time.split(":");
        String timeToString = temp2[0] + ":" + temp2[1];
        return timeToString;
    }


    public static void intentView(Context context, Class context2) {
        Intent intent = new Intent(context, context2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

//    public static String getDayFromDateString(String stringDate,String dateTimeFormat)
//    {
//        String[] daysArray = new String[] {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
//        String day = "";
//
//        int dayOfWeek =0;
//        //dateTimeFormat = yyyy-MM-dd HH:mm:ss
//        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
//        Date date;
//        try {
//            date = formatter.parse(stringDate);
//            Calendar c = Calendar.getInstance();
//            c.setTime(date);
//            dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;
//            if (dayOfWeek < 0) {
//                dayOfWeek += 7;
//            }
//            day = daysArray[dayOfWeek];
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return day;
//    }

    public static int getRange(Date startDate, Date endDate) {
        if (endDate != null && startDate != null) {
            long different = endDate.getTime() - startDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;

            int range = (int) elapsedDays;
            return range;
        } else {
            return 0;
        }
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    private static int calculateSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
