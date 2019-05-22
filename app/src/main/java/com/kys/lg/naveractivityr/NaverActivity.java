package com.kys.lg.naveractivityr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NaverActivity extends AppCompatActivity {

    ListView mylistvew;
    EditText search;
    Button btn_search;
    Parser parser;
    ArrayList<BookVO> arrayList;
    ViewModelAdapter viewModelAdapter;

    int start=1;//검색을 시작할 번호

    LayoutInflater inflater;
    View footerView;
    //
    boolean mLockListView=true;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver);

        mylistvew=findViewById(R.id.mainactivity_listview_mylistview);
        search=findViewById(R.id.mainacrtivity_edittext_search);
        btn_search=findViewById(R.id.mainactivity_button_search);
        dialog= new ProgressDialog(this);
        dialog.setMessage("로딩중...");
        dialog.setCancelable(false);




        parser= new Parser();

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(search.getText().toString().trim().length()!=0) {

                    //로딩 시작
                    dialog.show();

                    start = 1;
                    arrayList = new ArrayList<>();
                    viewModelAdapter = null;

                    new NaverAsync().execute(search.getText().toString()); //doingBackground() 호출

                }
                else{
                    Toast.makeText(getApplicationContext(),"검색어를 입력하세요",Toast.LENGTH_SHORT).show();
                }
            }


        });

        inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        footerView=inflater.inflate(R.layout.footer,null);

    }//onCreate()

    //AsyncTask클래스는 세개의 제너릭 타입을 가지고 있다.
    //1.doInBackground()의 파라미터로 전달할 자료형 타입
    //2.UI의 진행 상태를 과닐하는 onProgressUpdate()가 오버라이딩 되어있는 경우
    // 이 메서드에서 사용할 자료형 타입 ,안 씀
    //3.작업결과를 반영하는 onPostEcecute()의 파라미터 타입
    class NaverAsync extends AsyncTask <String,Void,ArrayList<BookVO>>{

        @Override
        protected ArrayList<BookVO> doInBackground(String... strings) {
            //각종 반복이나 제어등 주된 처리 로직을 담당하는 메서드
            //strings[0]---->검색어;
            //String ... :길이에 제한이 없는 배열

            return parser.connectNaver(start,strings[0],arrayList);
            //백그라운드에서 검색어와 리스트를 파라미터를 갖는다.

        }

        @Override
        protected void onPostExecute(ArrayList<BookVO> bookVOS) {
           //doInBackground()에서 작업을 마친 결과가
            //onPostExecute()의 파라미터로 전달된다.

            if(viewModelAdapter==null){
                viewModelAdapter=new ViewModelAdapter(NaverActivity.this,R.layout.book_item,bookVOS,mylistvew);

                //리스트뷰에 스크롤 감지자를 등록
                mylistvew.setOnScrollListener(scroollListener);

                //리스트뷰에 footer 등록(setAdapter 전에!!)
                mylistvew.addFooterView(footerView);
                //리스트뷰에 어댑터를 탑재
                mylistvew.setAdapter(viewModelAdapter);
            }

            viewModelAdapter.notifyDataSetChanged();
            mLockListView=false;

            //로딩종료
            dialog.dismiss();

        }
    }//NaverAsync

    //리스트뷰 스크롤 감지

    AbsListView.OnScrollListener scroollListener= new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //onScrollStateChanged 메서드는 현재 리스트뷰의 상태를 알려준다.
            //i로 넘어오는 상태값이 다음의 세가지가 있다.
            //SCROLL_STATE_FLING: 터치 후 손을 뗀 상태지만 아직 스크롤이 되고 있는 경우
            //scroll_state_idle:스크롤이 완전히 종료되어 어떠한 애니메이션도 발생하지 않는 경우
            //SCROLL_STATE_TOUCH_SCROLL:스크린에 터치를 한 상태롤 스크롤이 발생하는 경우
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //onScroll()메서드는 스크롤이 발생하는 동안 지속적으로 호출되는 메서드
            //firstVisibleItem : 현재 보여주는 리스트에서 최 상단 항목의 index
            //visibleItemCount : 현재 리스트뷰에서 보여주고 있는 항목의 수
            //totalItemCount   : 리스트뷰의 총 항목 수

            int count= totalItemCount - visibleItemCount;
            //count가 firstvisibleitem가 같을때 스크롤을 끝까지 올림.

            if(firstVisibleItem >=count &&
                    totalItemCount!=0
                    && mLockListView==false){

                mLockListView=true;

                if(start <1000 - Util.SEARCH_COUNT && arrayList.size()>=Util.SEARCH_COUNT){ //1000개만 검색한다.
                    if(start>=arrayList.size()){
                        //더이상 검색할게 없을 때 다음부터는 로드할 필요가 없게 만들어준다.
                        start=1000-Util.SEARCH_COUNT;

                    }

                    start+=Util.SEARCH_COUNT;

                    //통신을 한다.
                    //자동 등록
                    //new NaverAsync().execute(search.getText().toString());

                    footerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new NaverAsync().execute(search.getText().toString());
                        }
                    });

                }else{

                    Toast.makeText(getApplicationContext(),"더 불러올 내용이 없습니다.",Toast.LENGTH_SHORT).show();
                    //footer 제거
                    mylistvew.removeFooterView(footerView);

                }


            }

        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(NaverActivity.this);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NaverActivity.this.finish();

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setTitle("Book Search");
        builder.setMessage("종료하시겠습니까? ");
        builder.show();
    }
}
