
# Graduation_Personal-recommendation

graduation work / 개인형 배달음식 추천 시스템

졸업작품으로 개인형 배달음식 추천 시스템을 개발하였습니다.

주문빈도수가 적을 시 머신러닝을 통해 나와 비슷한 정보를 가진 사람들이 선호하는 메뉴들을 분석하고
요일 / 시간대별로 다섯가지 메뉴 추천을 받습니다.

주문빈도수가 어느정도 채워지면 다섯가지 추천 중 1,2순위는 개인빈도수로만 추천해주고,
나머지 3,4,5순위는 개인빈도수와 비교되는 머신러닝을 통한 분석모델이 활용됩니다.

listview를 사용하여 시간/요일이 달라짐에 따라 각자 다른 추천메뉴 다섯가지를 받아오게 하였고,
토스 api 를 활용하여 결제시스템을 test로 사용하였습니다.

추천시 활용되는건 사용자의 선호메뉴 뿐 아니라 gps를 활용한 현재 위치부터 집까지의 도착시간과,
가게의 배달시간이 가장 일치하는 메뉴로 순위를 선정하였습니다. 그 과정에서 네이버 api 를 통한 주소->좌표 변환 기능, 그리고 출발지와 목적지의 거리 및 시간 계산 기능을 사용하였습니다.

그 후 웹소켓을 활용하여 소비자<->사장님 어플의 실시간 양방향 통신이 가능하게하였습니다.

## 로그인화면
<img width="354" alt="image" src="https://github.com/kkh725/Android_Personal-food-recommendation/assets/120651330/b940668f-cffe-4703-8e45-dc37c9a0d16e">

## 추천 진행화면
<img width="340" alt="스크린샷 2024-01-08 오후 5 16 03" src="https://github.com/kkh725/Android_Personal-food-recommendation/assets/120651330/70aa896b-614d-479f-b083-c24c96b6eae0">


## 추천화면 
<img width="394" alt="스크린샷 2023-10-22 오후 7 42 07" src="https://github.com/kkh725/Android_Personal-food-recommendation/assets/120651330/f752529f-affd-4790-86ff-04f9d02e46d3">

## 웹소켓을 활용한 실시간 혼잡도 반영 
----------
사장님 어플에서 

https://github.com/kkh725/Android_Personal-food-recommendation/assets/120651330/6c630fad-cf23-451e-aee5-d3446935479a

혼잡도를 체크하는 버튼을 누를때마다 소비자 어플에서도 반영되는걸 확인


## 결제화면.
### 귀가시간과 배달시간을 계산하여 화면에 나타내고, 둘의 차이를 보여줌
---------

귀가시간은 gps 를 활용한 현재위치에서부터 사용자가 저장해놓은 주소까지의 소요시간.
배달시간은
- 가게에서부터 집까지의 소요시간
- 메뉴의 조리시간
- 혼잡도 (매우혼잡,혼잡,보통,여유) 으로 인한 지연시간
  
을 모두 합한 시간

<img width="354" alt="image" src="https://github.com/kkh725/Android_Personal-food-recommendation/assets/120651330/c36b62fa-e586-42af-b94b-806add0a7ef2">









