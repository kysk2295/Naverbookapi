package com.kys.lg.naveractivityr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;

public class ViewModelAdapter extends ArrayAdapter<BookVO>{

    Context context;
    ArrayList<BookVO> list;
    BookVO vo;
    int resource;

    public ViewModelAdapter(@NonNull Context context, int resource, ArrayList<BookVO> list, ListView myListview) {
        super(context, resource,list);

        this.context=context;
        this.resource=resource;
        this.list=list;

        //리스트뷰의 항목 클릭이벤트 감지자
        myListview.setOnItemClickListener(listclick);
    }//생성자

    AdapterView.OnItemClickListener listclick= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //https://book.naver.com/bookdb/book_detail.nhn?bid=
            String bookImg=list.get(position).getB_img();
            //이미지 경로에서 이름만 출력
            String bookid = bookImg.substring(bookImg.lastIndexOf("/")+1,bookImg.lastIndexOf(".jpg"));

            //추출한 이름을 통해 상세정보 페이지로 전환활 url
            String bookLink="https://book.naver.com/bookdb/book_detail.nhn?bid="+bookid;

            Intent intent= new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(bookLink));
            context.startActivity(intent);

        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView= inflater.inflate(resource,null);
        vo=list.get(position);

        TextView title= convertView.findViewById(R.id.book_titile);
        TextView author= convertView.findViewById(R.id.book_author);
        TextView price=convertView.findViewById(R.id.book_price);
        ImageView img=convertView.findViewById(R.id.book_img);

        title.setText("제목 : "+vo.getB_title());
        author.setText("저자 : "+vo.getB_auth());
        price.setText("가격 : "+vo.getB_price());

        //이미지 로드
        new ImgAsync(img,vo).execute();

        return convertView;
        //convertview가 항목 1칸: book_item.xml과 같다.
    }//getview()

    class ImgAsync extends AsyncTask<Void, Void,Bitmap>{

        Bitmap bitmap;
        ImageView img;
        BookVO vo;

        public ImgAsync(ImageView img, BookVO vo) {
            this.img = img;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            try{
                URL img_url= new URL(vo.getB_img());
                //이미지를 bite단위로 가져온다.
                BufferedInputStream bis= new BufferedInputStream(img_url.openStream());
                //다시 이미지를 bite에서 비트맵으로 조합한다.
                bitmap= BitmapFactory.decodeStream(bis);
                bis.close();

            }catch (Exception e){

            }
            if(bitmap!=null){
                return bitmap;
            }else{
                //가져올 이미지가 없다면 준비해둔 기본이미지를 사용
                Bitmap bm= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);

                return bm;

            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            //bitmap을 imageview에 세팅
            img.setImageBitmap(bitmap);
        }
    }
}
