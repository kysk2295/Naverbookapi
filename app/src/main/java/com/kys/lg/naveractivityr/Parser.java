package com.kys.lg.naveractivityr;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser  {
//웹에서 요소(제목,저자,가격...)를 검색하여 준비해노온 vo객체에 저장
    BookVO vo;
    String query="";

    public ArrayList<BookVO> connectNaver(int start,String myQuery, ArrayList<BookVO> list){

        try{

            query= URLEncoder.encode(myQuery,"utf8");
            //네이버에 요청할때 쿼리가 간다.
            int count =Util.SEARCH_COUNT;//검색 결과 10건 표시
            String urlStr="https://openapi.naver.com/v1/search/book.xml?query="+query+"&start="+start+"&display="+count;
            //URL클래스를 생성하여 위의 경로로 접근

            URL url= new URL(urlStr);

            //url클래스의 연결 정보를 connection 에게 전달
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();

            //네이버 오픈 API는 GET방식 지원(요청하는 정보가 노출됨)
            connection.setRequestMethod("GET");

            //네이버 인증 처리
            //발급받은 ID
            connection.setRequestProperty("X-Naver-Client-Id","M1PXHOPcbMfcQixbJNj9");
            //발급받은 secret
            connection.setRequestProperty("X-Naver-Client-Secret","Kkvq6JdzMM");

            //위의 URL을 수행하여 받아올 자원을 대입할 객체
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser parser= factory.newPullParser();
            parser.setInput(connection.getInputStream(),null);//결과물을 inputstream으로 읽어옴 parser:xml을 들고온다.


            //파서를 통하여 각 요소들을 반복수행 처리
            int parserEvent= parser.getEventType();
            while (parserEvent !=XmlPullParser.END_DOCUMENT){


                if(parserEvent==XmlPullParser.START_TAG) {

                    String tagName= parser.getName();
                    if(tagName.equalsIgnoreCase("title")) { //title태그 발견
                        vo=new BookVO();
                        String title= parser.nextText();

                        //네이버는 검색어의 강조를 위해<b> 태그를 붙여서 결과를
                        //반환하는데, 이것을 제거하기 위해 정규식을 사용
                        //java.utill.package
                        Pattern pattern= Pattern.compile("<.*?>");//한글자 태그 패턴
                        Matcher matcher= pattern.matcher(title);
                        if(matcher.find()){
                            String s_title=matcher.replaceAll("");
                            vo.setB_title(s_title);
                        }else{
                            vo.setB_title(title);
                        }

                    }else if(tagName.equalsIgnoreCase("image")){
                        String img= parser.nextText();
                        vo.setB_img(img);
                    }else if(tagName.equalsIgnoreCase("author")){

                        String author=parser.nextText();

                        Pattern pattern= Pattern.compile("<.*?>");//한글자 태그 패턴
                        Matcher matcher= pattern.matcher(author);
                        if(matcher.find()){
                            String s_author=matcher.replaceAll("");
                            vo.setB_auth(s_author);
                        }else{
                            vo.setB_auth(author);
                        }

                    }else if(tagName.equalsIgnoreCase("price")){
                        String price=parser.nextText();
                        vo.setB_price(price);
                        list.add(vo);
                    }

                }
                parserEvent=parser.next();//다음요소, 커서가 다음칸으로 내려감
                //END_DOCUMENT:문서의 끝
                //Xml의 끝을 만날때 까지 반복
            }//while

        }catch(Exception e){



        }
        return list;
    }//connectNaver()

}
