package config.Variables;

public class Variables {
	public static int samplingMinDistance = 50;
	public static int samplingMinTime = 30000;

	public static int isAutoUpload = 1;
	public static double frequencyInHours = 0.5;

	public static boolean powerSaving = true;
	public static boolean equiDistance = true;

	public static boolean areAnnotationsAllowed = false;
	public static boolean isAccelerometerEmbedded = true;
	public static String annotationsStrings = "Walk!__!Bicycle!__!Car!__!Bus!__!Subway!__!Train!__!Ferry";

	public static long acceleromterFrequency = 30000;
	public static long accelerometerSleepTime = 90000; // 60000*5 -> 5
															// minutes
	public static boolean isAccuracyFilterEnabled = false;
	public static float accuracyFilterValue = 250;
	public static boolean periodAnnotations = true;
	
	public static double speedTreshold = 2.5;
	
	public static double speed = 0.5;
	public static int stepsThresh = 5;

	   public static final String urlConnection = "ENTER_YOUR_HOSTED_MEILI_HTTP_ENDPOINT/users/";


	public static final String aboutSectionString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam magna tellus, pulvinar ut lacus vel, condimentum ullamcorper diam. Aenean ac erat luctus, consequat turpis nec, tempus quam. Aliquam volutpat mauris eget tortor cursus, eget laoreet turpis ullamcorper. Pellentesque non erat nec orci commodo adipiscing eget ac turpis. Suspendisse dignissim justo in dui tempus gravida. Sed id ante bibendum, laoreet diam nec, elementum neque. Donec accumsan nulla et massa suscipit, non vestibulum justo consequat. Quisque sollicitudin aliquam vehicula. Maecenas elit risus, venenatis in tincidunt et, bibendum mollis nisl. Curabitur laoreet arcu ut vehicula convallis. Proin quis elit non diam congue iaculis. Phasellus neque mi, elementum ut augue vitae, eleifend placerat turpis. Cras libero urna, varius id orci tempus, porttitor congue lacus. Nulla facilisi. Aliquam eu orci condimentum sem laoreet malesuada sed eu quam. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus."
			+ "-Vivamus mollis tincidunt leo. Maecenas tellus urna, rhoncus in diam ut, feugiat rutrum sem. Maecenas vel tincidunt dui. In libero erat, ultricies quis rhoncus eget, tincidunt at eros. Sed vitae massa eu augue dignissim laoreet ac nec nisl. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla commodo dolor in ultricies adipiscing. Cras id imperdiet mi. Phasellus hendrerit tortor eu leo rhoncus porta. Vestibulum dignissim sapien velit, eget molestie nisi euismod ut. Mauris non lacus sed elit vulputate convallis. Pellentesque et nunc sit amet erat iaculis tincidunt. Duis sit amet nibh sit amet nisl aliquet rhoncus. Vivamus tempus mi non libero aliquam, nec vestibulum libero pulvinar.";

	public static final String admin = "kth";
	public static final String password = "admin";
	public static String userLoginEndpoint = "loginUser";
	public static String locationUploadEndpoint = "insertLocationsAndroid";
	public static String userRegisterEndpoint = "/registerUser";
}
