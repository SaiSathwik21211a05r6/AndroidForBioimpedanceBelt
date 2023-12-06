
/**    noparts = finalMsg.split(",");

for (String part : noparts) {
lines1=part.split(":");

for(String line2:lines1)
{
lines2=line2.split(" ");
String[] lines4 = line2.split("\n");

for(String line3:lines2) {
Log.d("line3 is",line3);
if (isDecimal(line3)) {

if (rzMagValues.size() < 3 ) {
if(Double.valueOf(line3) > 100.00)rzMagValues.add(Double.valueOf(line3));

} else if (rzMagValues.size() >= 3 && Double.valueOf(line3) > 100.00) {
rzMagValues.clear();
rzMagValues.add(Double.valueOf(line3));

}


}

}
for(String line3:lines4) {


if (isDecimal(line3.trim())) {

if (rzPhaseValues.size() < 3&& Double.valueOf(line3)>70 && Double.valueOf(line3) < 100.00) {

rzPhaseValues.add(Double.valueOf(line3.trim()));



} else if (rzMagValues.size() >= 3&&Double.valueOf(line3)>70&& Double.valueOf(line3) < 100.00) {
rzPhaseValues.clear();
rzPhaseValues.add(Double.valueOf(line3.trim()));
}


}
}
}*/


/**   for (int i = 0; i < rzMagValues.size(); i++) {
double normalizedRzMagValue = normalizeValue(rzMagValues.get(i), 150000, 100);
rzMagValues.set(i, normalizedRzMagValue);
}
 */
/**    long currentTime1 = System.currentTimeMillis();
long timeElapsed = currentTime1 - startTime;
CustomDataPoint dataPoint = new CustomDataPoint(timeElapsed, rzMagValues, rzPhaseValues);
dataPoints.add(dataPoint);

// Update your data sets



// Update the chart with the new data
final String[] data = {null};
if(rzMagValues.size()!=0&&rzPhaseValues.size()!=0)
data[0] = new String(rzMagValues.get(0).toString().concat(","+rzPhaseValues.get(0).toString()));
 */    //  Log.d("data passed", data[0]);
// UI-related code to update the chart goes here
// updateChartWithData(dataPoints);

// Update your UI here

/**  adapter.updateData(data[0]);

adapter.setRecyclerView(recyclerView);*/
//  Log.d("received data1",receivedData1.toString());
/**     if(p1!=5)
p1=p1+1;
else
{if(p2!=5)
p2=p2+1;}*/
/** private void send(byte[] data) {
if (connected != Connected.True) {
Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
return;
}
try {
SpannableStringBuilder spn = new SpannableStringBuilder();
spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
service.write(data); // Send the byte array directly
// Optionally, you can log or display the sent data here.
Log.d("written data is ", Arrays.toString(data));
receiveText.append(spn); // Append any debug information as needed.
} catch (SerialTimeoutException e) {
status("write timeout: " + e.getMessage());
} catch (Exception e) {
onSerialIoError(e);
}
}*/
/** ValueFormatter customFormatter = new ValueFormatter() {
@Override
public String getFormattedValue(float value, AxisBase axis) {
// Calculate the visible range and the starting frequency
int visibleRange = (int) axis.getAxisMaximum() - (int) axis.getAxisMinimum();
int startingFrequency = (int) axis.getAxisMinimum();

// Calculate the electrode combination pair for the current label
int electrodePair = ((int) value - startingFrequency) % 6 + 1; // Adjust as needed

// Calculate the current frequency
int currentFrequency = (int) value;

// Set the maximum number of electrode pairs to show at a time
int electrodePairsToShow = 5;

// Check if the label is within the visible range and limited to electrodePairsToShow
if (electrodePair <= electrodePairsToShow) {
Log.d("value formatted",currentFrequency + " (" + (electrodePair / 6 + 1) + "," + (electrodePair % 6 + 1) + ")");
return currentFrequency + " (" + (electrodePair / 6 + 1) + "," + (electrodePair % 6 + 1) + ")";
} else {
return ""; // Empty string for labels outside the visible range or electrodePairsToShow
}
}
};
 */
