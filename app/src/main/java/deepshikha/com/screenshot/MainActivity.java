package deepshikha.com.screenshot;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    Button btn_screenshot;
    ScrollView scrollView;
    LinearLayout ll_linear;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    boolean boolean_save;
File pdfFolder,myPDF,imagePath;
    static Image image;
    static ImageView img;
    Bitmap bmp;
    static Bitmap bt;
    static byte[] bArray;
    FileOutputStream fos;
    Bitmap bitmap;
    private static boolean  isPortrait = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fn_permission();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void init() {
        btn_screenshot = (Button) findViewById(R.id.btn_screenshot);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        ll_linear = (LinearLayout) findViewById(R.id.ll_linear);
        ll_linear.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        btn_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boolean_save) {
                    Intent intent = new Intent(getApplicationContext(),Screenshot.class);
                    startActivity(intent);

                }else {
                    if (boolean_permission) {
                        Bitmap bitmap1 = loadBitmapFromView(ll_linear, ll_linear.getWidth(), ll_linear.getHeight());
                        saveBitmap(bitmap1);

                    } else {

                    }
                }

            }
        });
    }

    public void saveBitmap(Bitmap bitmap) {

         imagePath = new File(Environment.getExternalStorageDirectory(),
                 System.currentTimeMillis() + ".pdf");

        try {

            Document document ;
            if (isPortrait)
                document = new Document(PageSize.A4, 10, 10, 10, 10);
            else
                document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
            fos = new FileOutputStream(imagePath);
            PdfWriter.getInstance(document, fos);
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bArray=stream.toByteArray();
            addImage(document);
            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            View promptsView = li.inflate(R.layout.layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text

                                    String fileName = userInput.getText().toString();
                                    //myPDF = new File(pdfFolder + "/" + fileName + ".pdf");
                                    File newFile = new File(pdfFolder + "/" + fileName + ".pdf");
                                    boolean result = imagePath.renameTo(newFile);

                                    imagePath = newFile;

                                  /*  Log.w(TAG, "myPDF renamed to: " + myPDF.toString() );*/
                                    promptForNextAction();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                    promptForNextAction();


                                }
                            });

            // create alert dialog
            //AlertDialog alertDialog = alertDialogBuilder.create();

            // show it

          /*  Log.e(TAG, "Before alertdialogue.show");*/


            alertDialogBuilder.show();






            document.close();
           /* fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();*/
            Toast.makeText(getApplicationContext(),imagePath.getAbsolutePath()+"",Toast.LENGTH_SHORT).show();
            boolean_save = true;

            btn_screenshot.setText("Check image");

            Log.e("ImageSave", "Saveimage");
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void promptForNextAction()
    {
        final String[] options = { "email", "preview","Whatsapp",
                "cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("PDF Saved, What Next?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("email")) {
                    emailNote();
                } else if (options[which].equals("preview")) {
                    viewPdf();

                }
                else if (options[which].equals("Whatsapp")) {
                    whatsapp();
                } else if (options[which].equals("cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
       /* Log.w(TAG, "Opening:  " + myPDF.toString());*/
        intent.setDataAndType(Uri.fromFile(imagePath), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void emailNote()
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        //email.putExtra(Intent.EXTRA_SUBJECT,"hello world");
        //email.putExtra(Intent.EXTRA_TEXT, "hello world");
        Uri uri = Uri.parse(imagePath.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("text/plain");
        email.putExtra(Intent.EXTRA_SUBJECT, " ");
        email.putExtra(Intent.EXTRA_TEXT, "");
        email.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(imagePath));
        email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /*email.setType("application/pdf");*/
        startActivity(email);
    }

    private void whatsapp()
    {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(imagePath.getAbsolutePath());
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
       /* sendIntent.setType("text/plain");*/
        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
        sendIntent.setType("application/pdf");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.setBackgroundColor(Color.WHITE);
        v.draw(c);

        return b;
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    private  void addImage(Document document)
    {

        try
        {

            pdfFolder = new File(Environment.getExternalStorageDirectory(), "EasyConvert"); // check this warning, may be important for diff API levels

            //ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);

            // if the directory doesn't already exist, create it
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
               /* Log.i(TAG, "Folder successfully created");*/
            }
            image = Image.getInstance(bArray);///Here i set byte array..you can do bitmap to byte array and set in image...

            if (isPortrait) {
                image.scaleToFit(PageSize.A4);
                image.setAbsolutePosition(
                        (PageSize.A4.getWidth() - image.getScaledWidth())/2 ,
                        (PageSize.A4.getHeight() - image.getScaledHeight())/2
                );
            }
            else
            {
                image.scaleToFit(PageSize.A4.rotate());
                image.setAbsolutePosition(
                        (PageSize.A4.rotate().getWidth() - image.getScaledWidth())/2 ,
                        (PageSize.A4.rotate().getHeight() - image.getScaledHeight())/2
                );
            }
        }
        catch (BadElementException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


