import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.List;

public class CarReport {

    private static final int REQUEST_ADD_DATA = 0;
    private static final Object TAG = null;
    private Save mFuelType;
    private Save mEdtCategory;
    private Save mEdtName;
    private List<Object> mOtherFuelTypeNames;
    private Button mBtnOk;
    private View mTxtTrustCertificateDescription;
    private View mTxtTrustCertificate;
    private View mChkTrustCertificate;
    private EditText mEdtUserName;
    private EditText mEdtPassword;
    private EditText mEdtUrl;
    private AnimatorSet mFullScreenChartAnimator;
    private ComboLineColumnChartView mCurrentFullScreenChart;
    private View mFullScreenChart;
    private Rect mCurrentFullScreenStartBounds;
    private float mCurrentFullScreenStartScaleX;
    private float mCurrentFullScreenStartScaleY;
    private ComboLineColumnChartView mFullScreenChartHolder;
    private View android;
    private ComboLineColumnChartView mAppBarLayout;
    private View mDbxClient;

    // me.kuehle.carreport.gui.dialog.EditFuelTypeDialogFragment.save()
    private boolean save() {
        FormValidator validator = new FormValidator();
        validator.add(new FormFieldNotEmptyValidator(mEdtName));
        validator.add(new AbstractFormFieldValidator(mEdtName) {
//            @Override // Removed to allow compilation
            protected boolean isValid() {
                String name = mEdtName.getText().toString();
                return !mOtherFuelTypeNames.contains(name);
            }

            //            @Override // Removed to allow compilation
            protected int getMessage() {
                return R.string.validate_error_fuel_type_exists;
            }
        });
        validator.add(new FormFieldNotEmptyValidator(mEdtCategory));

        if (validator.validate()) {
            FuelTypeContentValues values = new FuelTypeContentValues();
            values.putName(mEdtName.getText().toString());
            values.putCategory(mEdtCategory.getText().toString());

            if (mFuelType == null) {
                values.insert(getActivity().getContentResolver());
            } else {
                FuelTypeSelection where = new FuelTypeSelection().id(mFuelType.getId());
                values.update(getActivity().getContentResolver(), where);
            }

            return true;
        } else {
            return false;
        }
    }

    private static class Utils {
        public String getCacheDir() {
            return null;
        }
    }

    public class SetupWebDavSyncDialogActivity extends Activity {
        // me.kuehle.carreport.gui.dialog.SetupWebDavSyncDialogActivity.onCreate(android.os.Bundle)
    //    @Override // Removed to allow compilation
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_webdav_sync);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mEdtUrl = (EditText) findViewById(R.id.edt_url);
            mEdtUrl.addTextChangedListener(new TextWatcher() {
                //    @Override // Removed to allow compilation
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                //    @Override // Removed to allow compilation
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                //    @Override // Removed to allow compilation
                public void afterTextChanged(Editable s) {
                    mTxtTrustCertificateDescription.setVisibility(View.GONE);
                    mTxtTrustCertificate.setVisibility(View.GONE);
                    mChkTrustCertificate.setChecked(false);
                    mChkTrustCertificate.setVisibility(View.GONE);
                }
            });
            mEdtUserName = (EditText) findViewById(R.id.edt_user_name);
            mEdtPassword = (EditText) findViewById(R.id.edt_password);
            mTxtTrustCertificateDescription = (TextView) findViewById(R.id.txt_trust_certificate_description);
            mTxtTrustCertificate = (TextView) findViewById(R.id.txt_trust_certificate);
            mChkTrustCertificate = (CheckBox) findViewById(R.id.chk_trust_certificate);

            mTxtTrustCertificateDescription.setVisibility(View.GONE);
            mTxtTrustCertificate.setVisibility(View.GONE);
            mChkTrustCertificate.setVisibility(View.GONE);

            mBtnOk = (Button) findViewById(R.id.btn_ok);
            mBtnOk.setOnClickListener(new View.OnClickListener() {
                //    @Override // Removed to allow compilation
                public void onClick(View v) {
                    onOkClick();
                }
            });
            findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                //    @Override // Removed to allow compilation
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }
    }

    // me.kuehle.carreport.gui.MainActivity.handleFABClick(int,int)
    private void handleFABClick(final int edit, final int otherType) {
        closeFABMenu();

        Preferences prefs = new Preferences(this);
        CarCursor car = new CarSelection().suspendedSince((Date) null).query(getContentResolver(),
                CarColumns.ALL_COLUMNS, CarColumns.NAME + " COLLATE UNICODE");

        if (car.getCount() == 1 || !prefs.isShowCarMenu()) {
            Intent intent = getDetailActivityIntent(edit, prefs.getDefaultCar(), otherType);
            startActivityForResult(intent, REQUEST_ADD_DATA + edit);
        } else {
            final long[] carIds = new long[car.getCount()];
            final String[] carNames = new String[car.getCount()];
            while (car.moveToNext()) {
                carIds[car.getPosition()] = car.getId();
                carNames[car.getPosition()] = car.getName();
            }

            new Builder(this)
                    .setItems(carNames, new DialogInterface.OnClickListener() {
                        //    @Override // Removed to allow compilation
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getDetailActivityIntent(edit, carIds[which], otherType);
                            startActivityForResult(intent, REQUEST_ADD_DATA + edit);
                        }
                    })
                    .create()
                    .show();
        }
    }

    // me.kuehle.carreport.gui.ReportFragment.showFullScreenChart(me.kuehle.carreport.data.report.AbstractReport,lecho.lib.hellocharts.view.ComboLineColumnChartView)
    private void showFullScreenChart(AbstractReport report, ComboLineColumnChartView v) {
        if (getView() == null) {
            return;
        }

        if (mFullScreenChartAnimator != null) {
            mFullScreenChartAnimator.cancel();
        }

        mCurrentFullScreenChart = v;

        ReportChartOptions options = loadReportChartOptions(getContext(), report);
        mFullScreenChart.setComboLineColumnChartData(report.getChartData(options));
        applyViewport(mFullScreenChart, false);

        // Calculate translation start and end point and scales.
        mCurrentFullScreenStartBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        mCurrentFullScreenChart.getGlobalVisibleRect(mCurrentFullScreenStartBounds);
        getView().getGlobalVisibleRect(finalBounds, globalOffset);
        mCurrentFullScreenStartBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        mCurrentFullScreenStartScaleX = (float) mCurrentFullScreenStartBounds
                .width() / finalBounds.width();
        mCurrentFullScreenStartScaleY = (float) mCurrentFullScreenStartBounds
                .height() / finalBounds.height();

        // Hide the small chart and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the small
        // chart.
        mCurrentFullScreenChart.setVisibility(View.INVISIBLE);
        mFullScreenChartHolder.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of the zoomed-in view (the default is the center of
        // the view).
        mFullScreenChartHolder.setPivotX(0f);
        mFullScreenChartHolder.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(mFullScreenChartHolder, View.X,
                        mCurrentFullScreenStartBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mFullScreenChartHolder, View.Y,
                        mCurrentFullScreenStartBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(mFullScreenChartHolder, View.SCALE_X,
                        mCurrentFullScreenStartScaleX, 1f))
                .with(ObjectAnimator.ofFloat(mFullScreenChartHolder, View.SCALE_Y,
                        mCurrentFullScreenStartScaleY, 1f));
        set.setDuration(getResources().getInteger(
                android.R.integer.config_longAnimTime));
        set.addListener(new AnimatorListenerAdapter() {
            //    @Override // Removed to allow compilation
            public void onAnimationEnd(Animator animation) {
                mFullScreenChartAnimator = null;
                mAppBarLayout.setVisibility(View.INVISIBLE);
            }
        });
        set.start();
        mFullScreenChartAnimator = set;
    }

    // me.kuehle.carreport.util.sync.provider.DropboxSyncProvider.downloadFile()
    //    @Override // Removed to allow compilation
    public void downloadFile() throws SyncIoException, SyncParseException {
        File localFile = getLocalFile();
        File tempFile = new File(Application.getContext().getCacheDir(), getClass().getSimpleName());

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            mDbxClient.files()
                    .download("/" + localFile.getName())
                    .download(outputStream);
            if (!FileCopyUtil.copyFile(tempFile, localFile)) {
                throw new IOException();
            }
        } catch (NetworkIOException e) {
            throw new SyncIoException(e);
        } catch (DbxException | IOException e) {
            throw new SyncParseException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close output stream after downloading file.", e);
                }
            }

            if (!tempFile.delete()) {
                Log.w(TAG, "Could not delete temp file after downloading.");
            }
        }
    }

    private File getLocalFile() {
        return null;
    }

    private View getResources() {
        return null;
    }

    private ComboLineColumnChartView getView() {
        return null;
    }

    private void applyViewport(View mFullScreenChart, boolean b) {

    }

    private ReportChartOptions loadReportChartOptions(Object context, AbstractReport report) {
        return null;
    }

    private Save getContext() {
        return null;
    }

    private Save getActivity() {
        return null;
    }

    private class Save {
        public int validate_error_fuel_type_exists;

        public Save getContentResolver() {
            return null;
        }

        public Object getId() {
            return null;
        }

        public Object getText() {
            return null;
        }

        public void getCacheDir() {

        }
    }

    private class FuelTypeContentValues {
        public void update(Save contentResolver, FuelTypeSelection where) {

        }

        public void insert(Save contentResolver) {

        }

        public void putCategory(String toString) {

        }

        public void putName(String toString) {

        }
    }

    private class FuelTypeSelection {
        public FuelTypeSelection id(Object id) {
            return null;
        }
    }

    private class FormValidator {
        public boolean validate() {
            return false;
        }

        public void add(AbstractFormFieldValidator formFieldNotEmptyValidator) {

        }

        public void add(FormFieldNotEmptyValidator formFieldNotEmptyValidator) {

        }
    }

    private class FormFieldNotEmptyValidator {
        public FormFieldNotEmptyValidator(Save mEdtName) {
        }
    }

    private class AbstractFormFieldValidator {
        public AbstractFormFieldValidator(Save mEdtName) {
        }
    }

    private static class R {
        public static Save string;
        public static R id;
        public static View layout;
        public Object edt_url;
        public Object edt_user_name;
        public Object edt_password;
        public Object txt_trust_certificate_description;
        public Object txt_trust_certificate;
        public Object chk_trust_certificate;
        public Object btn_ok;
        public Object btn_cancel;
    }

    private class Bundle {
    }

    private static class ViewGroup {
        public static class LayoutParams {
            public static final Object MATCH_PARENT = null;
            public static final Object WRAP_CONTENT = null;
        }
    }

    private class Editable {
    }

    private class EditText extends View {
        public void addTextChangedListener(TextWatcher textWatcher) {

        }
    }

    private static class View {
        public static final Object GONE = null;
        public static final Object INVISIBLE = null;
        public static final Object VISIBLE = null;
        public static final Object X = null;
        public static final Object Y = null;
        public static final Object SCALE_X = null;
        public static final Object SCALE_Y = null;
        public static final View R = null;
        public Object activity_setup_webdav_sync;
        public View integer;
        public Object config_longAnimTime;

        public void setOnClickListener(OnClickListener onClickListener) {

        }

        public void setVisibility(Object gone) {

        }

        public void setChecked(boolean b) {

        }

        public void setLayout(Object matchParent, Object wrapContent) {

        }

        public CarCursor query(Object contentResolver, Object allColumns, String s) {
            return null;
        }

        public void cancel() {

        }

        public void setComboLineColumnChartData(Object chartData) {

        }

        public View getInteger(Object config_longAnimTime) {
            return null;
        }

        public View files() throws NetworkIOException, DbxException {
            return null;
        }

        public View download(String s) {
            return null;
        }

        public View download(FileOutputStream outputStream) {
            return null;
        }

        public static class OnClickListener {
        }
        public void setOnClickListener() {

        }
    }

    private static class Activity {
        public static final Object RESULT_CANCELED = null;

        protected void onCreate(Bundle savedInstanceState) {

        }
    }

    private class TextView extends View {
    }

    private class CheckBox extends View {
    }

    private class Button extends View {
        public void setOnClickListener(OnClickListener onClickListener) {

        }
    }

    private class TextWatcher {
    }

    private void setContentView(Object activity_setup_webdav_sync) {

    }

    private View getWindow() {
        return null;
    }

    private void setResult(Object resultCanceled) {

    }

    private void onOkClick() {

    }

    private View findViewById(Object edt_url) {
        return null;
    }

    private void finish() {

    }

    private class Preferences {
        public Preferences(CarReport carReport) {
        }

        public long getDefaultCar() {
            return 0;
        }

        public boolean isShowCarMenu() {
            return false;
        }
    }

    private static class CarColumns {
        public static final Object ALL_COLUMNS = null;
        public static final String NAME = null;
    }

    private class Intent {
    }

    private class OnClickListener {
    }

    private static class DialogInterface {
        public static class OnClickListener {
        }
    }

    private class Builder {
        public Builder(CarReport carReport) {
        }

        public Builder setItems(String[] carNames, DialogInterface.OnClickListener onClickListener) {
            return null;
        }

        public Builder create() {
            return null;
        }

        public void show() {

        }
    }

    private class CarSelection {
        public View suspendedSince(Date date) {
            return null;
        }
    }

    private class CarCursor {
        public long getId() {
            return 0;
        }

        public String getName() {
            return null;
        }

        public int getCount() {
            return 0;
        }

        public int getPosition() {
            return 0;
        }

        public boolean moveToNext() {
            return false;
        }
    }

    private Object getContentResolver() {
        return null;
    }

    private void startActivityForResult(Intent intent, int i) {

    }

    private Intent getDetailActivityIntent(int edit, long carId, int otherType) {
        return null;
    }

    private void closeFABMenu() {

    }

    private class ComboLineColumnChartView {
        public void getGlobalVisibleRect(Rect mCurrentFullScreenStartBounds) {

        }

        public void getGlobalVisibleRect(Rect finalBounds, Point globalOffset) {

        }

        public void setVisibility(Object invisible) {

        }

        public void setPivotX(float v) {

        }

        public void setPivotY(float v) {

        }
    }

    private class AbstractReport {
        public Object getChartData(ReportChartOptions options) {
            return null;
        }
    }

    private class ReportChartOptions {
    }

    private class Rect {
        public Object left;
        public Object top;

        public void offset(int i, int i1) {

        }

        public float width() {
            return 0;
        }

        public float height() {
            return 0;
        }
    }

    private class AnimatorSet {
        public void setDuration(View integer) {

        }

        public void start() {

        }

        public void addListener(AnimatorListenerAdapter animatorListenerAdapter) {

        }

        public Temporal play(Object ofFloat) {
            return null;
        }

        public void cancel() {

        }
    }

    private static class ObjectAnimator {
        public static TemporalAdjuster ofFloat(ComboLineColumnChartView mFullScreenChartHolder, Object y, Object top, Object top1) {
            return null;
        }
    }

    private class Animator {
    }

    private class AnimatorListenerAdapter {
    }

    private class Point {
        public int x;
        public int y;
    }

    private class SyncIoException extends NetworkIOException {
        public SyncIoException(NetworkIOException e) {
        }
    }

    private class SyncParseException extends DbxException {
        public SyncParseException(Exception e) {

        }
    }

    private static class Application {
        public static Utils getContext() {
            return null;
        }
    }

    private static class FileCopyUtil {
        public static boolean copyFile(File tempFile, File localFile) {
            return false;
        }
    }

    private class NetworkIOException extends Exception{
    }

    private class DbxException extends Exception {
    }

    private static class Log {
        public static void e(Object tag, String s, IOException e) {

        }

        public static void w(Object tag, String s) {

        }
    }
}
