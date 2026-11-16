-- 08_movies_tmdb.sql : 샘플 영화 40편(work_id 1~40)을 TMDB 실제 데이터로 채운다 (2026-11-16)
-- 바꾸는 것: 개봉일, 줄거리, 포스터, 상영 시간, source='TMDB' + external_id(TMDB 영화 ID)
-- 그대로 두는 것: 작품 번호, 제목, 장르, 감독·배우 → 감상 기록·후기·통계는 영향 없음
-- 대조 방법: TMDB 검색(제목+연도) 후 감독 이름이 샘플과 같은지 확인(40편 모두 일치)
-- source IS NULL 조건: 이미 실행한 DB에서 다시 실행해도 아무것도 바뀌지 않는다.
-- 영화 정보 출처: TMDB (This product uses the TMDB API but is not endorsed or certified by TMDB.)

-- 1 기생충
UPDATE work SET release_date = '2019-05-30', summary = '가족 모두가 일자리를 구하지 못해 앞날이 막막한 기택의 집에, 장남 기우를 위한 고액 과외 제안이 들어온다. 명문대생 친구가 연결해 준 자리인 만큼, 기우에게는 오랜만에 찾아온 고정수입의 기회이자 가족의 기대를 짊어진 일이다. 온 가족의 도움을 받고 박 사장의 저택으로 향한 그는, 살던 곳과 전혀 다른 풍요롭고 정돈된 공간을 마주한다. 집주인의 아내 연교와 가정부 문광의 안내를 받은 기우는 딸 다혜의 수업을 시작하며 새로운 집안의 일상에 조심스레 들어간다. 과외가 순조롭게 진행되자 어렵게 열린 기회를 놓치고 싶지 않은 마음은 커지고, 두 가족의 생활은 조금씩 맞닿기 시작한다. 서로에게 필요한 것을 채워줄 듯한 이 만남은, 처음에는 보이지 않던 차이와 욕망을 드러내는데...', poster_url = 'https://image.tmdb.org/t/p/w500/jjHccoFjbqlfr4VGLVLT7yek0Xn.jpg', source = 'TMDB', external_id = '496243' WHERE work_id = 1 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 131 WHERE work_id = 1;
-- 2 올드보이
UPDATE work SET release_date = '2003-11-21', summary = '오대수는 어느 날 술이 거나하게 취해 집에 돌아가는 길에 누군가에게 납치, 사설 감금방에 갇히게 된다. 중국집 군만두만을 먹으며 8평이라는 제한된 공간에서 그가 할 수 있는 일이라곤 텔레비전 보는 게 전부. 1년이 지났을 무렵, 뉴스를 통해 나오는 아내의 살해소식. 게다가 아내의 살인범으로 자신이 지목되고 있음을 알게 된 오대수는 복수와 탈출을 위해 감금방 한쪽 구석을 쇠젓가락으로 파기 시작한다. 감금 15년을 맞이하는 해, 마침내 사람 몸 하나 빠져나갈 만큼의 탈출구가 생겼을 때, 어이없게도 15년 전 납치됐던 바로 그 장소로 풀려나 있는 자신을 발견하게 된다.', poster_url = 'https://image.tmdb.org/t/p/w500/xpa9ybm6tYGna5LseqSXvKpSSJn.jpg', source = 'TMDB', external_id = '670' WHERE work_id = 2 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 120 WHERE work_id = 2;
-- 3 살인의 추억
UPDATE work SET release_date = '2003-04-25', summary = '1986년 경기도. 젊은 여인이 무참히 강간, 살해당한 시체로 발견된다. 2개월 후 비슷한 수법의 강간살인사건이 연이어 발생하면서 사건은 세간의 주목을 받기 시작하고, 일대는 연쇄살인이라는 생소한 범죄의 공포에 휩싸인다. 사건 발생 지역에 특별수사본부가 설치되고, 수사본부는 구희봉 반장을 필두로 지역토박이 형사 박두만과 조용구, 그리고 서울 시경에서 자원해 온 서태윤이 배치된다. 육감과 폭력으로 수사를 진행하는 박형사와 증거와 과학수사를 추구하는 서형사는 수사 내내 마찰을 일으키는데...', poster_url = 'https://image.tmdb.org/t/p/w500/wrsf1ebQ6S4gVhjR9FJElfdxAjj.jpg', source = 'TMDB', external_id = '11423' WHERE work_id = 3 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 127 WHERE work_id = 3;
-- 4 괴물
UPDATE work SET release_date = '2006-07-27', summary = '한강 둔치의 작은 매점에서 아버지를 도우며 살아가는 강두는 딸 현서와 함께 평범한 하루를 보내던 중 정체불명의 괴물을 목격한다. 순식간에 사람들을 덮친 괴물은 달아나던 현서까지 낚아채 강물 속으로 사라지고, 강두의 가족은 아이를 잃었다는 절망에 빠진다. 괴물과 접촉했다는 이유로 병원에 격리된 강두에게 어느 밤 현서의 전화가 걸려오지만, 누구도 그의 말을 믿어주지 않는다. 딸이 살아 있다는 확신을 얻은 강두는 아버지와 두 동생을 설득해 병원을 빠져나와 수색에 나선다. 군경이 통제하는 한강에서 가족은 손에 쥔 얼마 안 되는 단서로 아이가 갇힌 장소를 찾아야 한다. 도움을 청할 곳마저 막힌 이들은 괴물보다 먼저 현서에게 닿기 위해 위험구역 안으로 들어간다.', poster_url = 'https://image.tmdb.org/t/p/w500/tqDQzYGi1EWHgUZY91YRSJLDJVf.jpg', source = 'TMDB', external_id = '1255' WHERE work_id = 4 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 119 WHERE work_id = 4;
-- 5 박쥐
UPDATE work SET release_date = '2009-04-30', summary = '환자들을 돌보던 신부 상현은 질병을 막을 백신 실험에 자원하지만 감염되고, 정체를 알 수 없는 피를 수혈받은 뒤 살아난다. 기적처럼 회복한 그를 찾아 사람들이 몰려드는 동안 상현은 햇빛을 견디지 못하고 피를 갈망하는 변화를 숨긴다. 살인을 피하며 신앙을 지키려 애쓰는 그는 어린 시절 친구 강우와 재회하고, 친구의 집을 드나들다 아내 태주에게 이끌린다. 남편과 시어머니에게 억눌려 살던 태주 역시 상현과의 만남에서 익숙한 생활을 벗어나고 싶은 욕망을 느낀다. 서로를 향한 감정이 커질수록 상현은 사제로서 넘지 말아야 할 선과 살아남기 위해 필요한 피 사이에서 갈등한다. 타인을 구하려 내놓았던 몸이 이제 타인을 원하게 된 순간, 그의 기도와 행동은 점점 다른 곳을 향한다.', poster_url = 'https://image.tmdb.org/t/p/w500/2CoIYuNwGmdhsV5CM8jRJ2sAZKe.jpg', source = 'TMDB', external_id = '22536' WHERE work_id = 5 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 133 WHERE work_id = 5;
-- 6 아가씨
UPDATE work SET release_date = '2016-06-01', summary = '일제강점기 조선, 소매치기로 살아가던 숙희는 일본 귀족을 사칭하는 사기꾼의 제안을 받고 외딴 저택의 하녀로 들어간다. 막대한 재산을 상속받을 아가씨 히데코는 엄격한 이모부의 감시 아래 책을 읽으며 제한된 생활을 한다. 가짜 백작은 히데코를 유혹해 재산을 차지하려 하고, 숙희에게는 아가씨가 그를 사랑하도록 부추기는 역할이 주어진다. 순박한 하녀처럼 행동하며 가까이에서 시중을 들던 숙희는 자신을 의지하는 히데코에게 점차 예상하지 못한 감정을 느낀다. 백작이 저택에 드나들며 계획을 진행할수록, 아가씨의 마음을 움직여야 하는 숙희는 자신이 품은 마음부터 숨기기 어려워진다. 서로 다른 욕망을 감춘 사람들이 한 지붕 아래 모인 가운데, 누가 누구를 속이고 있는지조차 흐려진다.', poster_url = 'https://image.tmdb.org/t/p/w500/3eo3rQyiW4XIr27oSjAv2d09WMA.jpg', source = 'TMDB', external_id = '290098' WHERE work_id = 6 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 144 WHERE work_id = 6;
-- 7 헤어질 결심
UPDATE work SET release_date = '2022-06-29', summary = '산 정상에서 추락한 한 남자의 변사 사건. 담당 형사 해준은 사망자의 아내 서래와 마주하게 된다. 남편의 죽음 앞에서 특별한 동요를 보이지 않는 서래. 경찰은 보통의 유가족과는 다른 서래를 용의선상에 올린다. 해준은 사건 당일의 알리바이 탐문과 신문, 잠복수사를 통해 서래를 알아가면서 그녀에 대한 관심이 점점 커져가는 것을 느낀다. 한편 좀처럼 속을 짐작하기 어려운 서래는 상대가 자신을 의심한다는 것을 알면서도 조금의 망설임도 없이 해준을 대하는데...', poster_url = 'https://image.tmdb.org/t/p/w500/rXEJ28XDQsogIGqwVEgwM2oDdpl.jpg', source = 'TMDB', external_id = '705996' WHERE work_id = 7 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 138 WHERE work_id = 7;
-- 8 부산행
UPDATE work SET release_date = '2016-07-20', summary = '일에 쫓겨 딸 수안과 멀어진 펀드매니저 석우는 생일 소원을 들어주려고, 엄마가 있는 부산으로 향하는 KTX에 함께 오른다. 출발 직전 다친 여자가 열차에 숨어들고, 곧 승무원을 덮치는 기이한 공격이 객차 안으로 번진다. 감염된 사람들이 다른 승객을 물어뜯으며 늘어나자, 평범했던 여행은 좁은 통로에서 목숨을 지켜야 하는 도주로 바뀐다. 석우는 임신한 아내를 지키는 상화와 야구부 학생 영국 등 낯선 이들과 힘을 합쳐, 떨어진 가족들에게 다가갈 방법을 찾는다. 전국의 재난 소식이 전해지는 가운데 승객들은 안전하다고 알려진 부산에 마지막 기대를 건다. 하지만 살아남으려는 공포가 사람들 사이의 불신으로 번지면서, 열차 안에서는 감염자와의 싸움만큼 문을 열어주는 일도 어려워진다.', poster_url = 'https://image.tmdb.org/t/p/w500/6XvEZVBFFjybvb1yQd1qfOC6F2S.jpg', source = 'TMDB', external_id = '396535' WHERE work_id = 8 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 118 WHERE work_id = 8;
-- 9 극한직업
UPDATE work SET release_date = '2019-01-23', summary = '불철주야 달리고 구르지만 실적은 바닥, 급기야 해체 위기를 맞는 마약반!  더 이상 물러설 곳이 없는 팀의 맏형 고반장은 국제 범죄조직의 국내 마약 밀반입 정황을 포착하고 장형사, 마형사, 영호, 재훈까지 4명의 팀원들과 함께 잠복 수사에 나선다.  마약반은 24시간 감시를 위해 범죄조직의 아지트 앞 치킨집을 인수해 위장 창업을 하게 되고, 뜻밖의 절대미각을 지닌 마형사의 숨은 재능으로 치킨집은 일약 맛집으로 입소문이 나기 시작한다.  수사는 뒷전, 치킨장사로 눈코 뜰 새 없이 바빠진 마약반에게 어느 날 절호의 기회가 찾아오는데…', poster_url = 'https://image.tmdb.org/t/p/w500/lzzb384pgYyr0GQvu3K2ZW62CEE.jpg', source = 'TMDB', external_id = '567646' WHERE work_id = 9 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 111 WHERE work_id = 9;
-- 10 명량
UPDATE work SET release_date = '2014-07-30', summary = '1597년, 오랜 전쟁과 연이은 패배로 조선 수군이 무너진 가운데 파면당했던 이순신이 다시 삼도수군통제사로 돌아온다. 그러나 그를 기다리는 것은 열두 척의 배와 전의를 잃은 병사들, 왜군의 진격 소식에 피란을 서두르는 백성들뿐이다. 압도적인 전력을 앞세운 적의 함대가 다가오고 잔혹한 장수 구루지마까지 가세하자, 수군을 포기해야 한다는 목소리도 높아진다. 이순신은 물러서면 육지의 백성들까지 위험해진다는 사실을 알기에, 극심한 두려움에 빠진 군사들을 다시 전장에 세울 방법을 찾는다. 거센 물살이 흐르는 명량 해협을 바라보는 그의 판단은 동료들조차 쉽게 받아들이기 어려운 결단으로 이어진다. 누구도 승산을 말하지 못하는 바다에서, 그는 먼저 한 척의 배를 앞으로 움직인다.', poster_url = 'https://image.tmdb.org/t/p/w500/fl3G8TprVzt4icbYyQKqdPmUfIO.jpg', source = 'TMDB', external_id = '282631' WHERE work_id = 10 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 128 WHERE work_id = 10;
-- 11 택시운전사
UPDATE work SET release_date = '2017-08-02', summary = '택시운전사 만섭은 아내를 여의고 11살 딸을 키우며 어렵게 살림을 꾸리는 가장. 외국손님을 태우고 광주에 갔다 통금 전에 돌아오면 밀린 월세를 갚을 수 있는 거금 10만원을 준다는 말에  독일기자 피터를 태우고 영문도 모른 채 길을 나선다. 피터는 독일 공영방송 소속 기자로, 일본에서 ‘광주가 심상치 않다’는 말을 듣고 광주로 향한다. 어떻게든 택시비를 받아야 하는 만섭의 기지로 검문을 뚫고 겨우 들어선 광주. 위험하니 서울로 돌아가자는 만섭의 만류에도 피터는 대학생 재식과 황기사의 도움 속에 촬영을 시작하는데...', poster_url = 'https://image.tmdb.org/t/p/w500/pCdx7X5JLaSJZLNVtpryGSdjvvB.jpg', source = 'TMDB', external_id = '437068' WHERE work_id = 11 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 137 WHERE work_id = 11;
-- 12 베테랑
UPDATE work SET release_date = '2015-08-05', summary = '광역수사대 형사 서도철은 한 번 맡은 사건은 끝까지 파고드는 집념으로 동료들과 함께 거친 범죄 현장을 누빈다. 큰 사건을 마친 뒤 참석한 자리에서 재벌 3세 조태오를 만난 그는 남을 거리낌 없이 짓밟는 태도에 불쾌한 인상을 받는다. 얼마 뒤 수사를 도왔던 화물차 기사 배씨가 조태오의 회사에서 추락했다는 소식이 들리고, 도철은 단순한 자살 시도라는 설명을 의심한다. 피해자의 사정을 알아볼수록 조태오와 그를 보좌하는 최상무가 사건에 얽혀 있다는 정황이 드러난다. 돈과 인맥으로 수사를 막으려는 회유가 이어지고 주변에서도 물러서라는 압박이 들어오지만, 도철은 동료들과 증거를 좇는다. 법보다 자신의 힘을 믿는 조태오 앞에서 도철은 이번만큼은 넘어갈 수 없는 선을 긋는다.', poster_url = 'https://image.tmdb.org/t/p/w500/8OJ4VtaddbDDwP1ydCitsYxgzCT.jpg', source = 'TMDB', external_id = '346646' WHERE work_id = 12 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 123 WHERE work_id = 12;
-- 13 범죄도시
UPDATE work SET release_date = '2017-10-03', summary = '2004년 서울 가리봉동, 형사 마석도는 거친 주먹과 현장의 감각으로 범죄 조직 사이의 충돌을 누르며 동네의 질서를 지켜 왔다. 그러던 중 중국 하얼빈에서 넘어온 신흥 조직이 잔혹한 폭력으로 기존 세력을 밀어내면서 거리의 분위기가 달라진다. 돈을 거둬들이는 데 수단을 가리지 않는 일당은 빠르게 영역을 넓히고, 지역의 강자인 황사장까지 위협한다. 주민들의 일상에 공포가 퍼지자 마석도와 전일만 반장이 이끄는 강력반은 더는 힘의 균형에 기댈 수 없다고 판단한다. 형사들은 거리의 정보와 조직들의 움직임을 모으며 예측하기 어려운 범인들을 한꺼번에 붙잡을 소탕 작전을 준비한다. 말보다 폭력이 먼저 오가는 동네에서 마석도는 익숙한 골목을 다시 경찰의 손에 돌려놓으려 한다.', poster_url = 'https://image.tmdb.org/t/p/w500/sVwbHA4nhw389PwpDzkJam4WMoB.jpg', source = 'TMDB', external_id = '479718' WHERE work_id = 13 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 121 WHERE work_id = 13;
-- 14 7번방의 선물
UPDATE work SET release_date = '2013-01-23', summary = '최악의 흉악범들이 모인 교도소 7번방에 이상한 놈이 들어왔다! 그는 바로 6살 지능의 딸바보 용구. 평생 죄만 짓고 살아온 7번방 패밀리들에게 떨어진 미션은 바로 용구 딸 예승이를 외부인 절대 출입금지인 교도소에 반.입.하.는.것! 웃음과 감동 가득한 사상초유의 합동작전이 시작된다!', poster_url = 'https://image.tmdb.org/t/p/w500/c9TqJPm4pZCuiEXumTayoNIrBSK.jpg', source = 'TMDB', external_id = '158445' WHERE work_id = 14 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 127 WHERE work_id = 14;
-- 15 타짜
UPDATE work SET release_date = '2006-09-28', summary = '가구공장에서 일하며 남루한 삶을 사는 고니는 대학보다 가난을 벗어나게 해줄 돈이 우선인 청년. 어느 날 고니는 가구공장 한 켠에서 박무석 일행이 벌이는 화투판에 끼게 되지만, 고니는 그 판에서 3년 동안 모아두었던 돈 전부를 날리고 만다. 그것이 전문 도박꾼들이 짜고 친 판이었단 사실을 뒤늦게 안 고니는 박무석 일행을 찾아 나서고, 도박으로 시비가 붙은 한 창고에서 우연인 듯 필연처럼 전설의 타짜 평경장을 만난다. 그리고 잃었던 돈의 다섯 배를 따면 화투를 그만두겠단 약속을 하고, 그와 함께 본격적인 꽃싸움에 몸을 던지기 위한 동행길에 오른다.', poster_url = 'https://image.tmdb.org/t/p/w500/o1a5FULQTObsNbcgjapJtunpFzV.jpg', source = 'TMDB', external_id = '38015' WHERE work_id = 15 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 139 WHERE work_id = 15;
-- 16 도둑들
UPDATE work SET release_date = '2012-07-25', summary = '미술관을 턴 뽀빠이의 팀은 옛 동료 마카오박의 제안을 받고, 막 출소한 금고털이 팹시와 함께 홍콩으로 향한다. 그곳에서 첸이 이끄는 중국 도둑들과 만난 이들은 협력을 약속하면서도 서로를 경계한다. 마카오박이 내놓은 목표는 마카오 카지노에 보관된 2천만 달러짜리 다이아몬드 태양의 눈물로, 각 분야 전문가의 손발이 맞아야 접근할 수 있다. 그러나 뽀빠이는 마카오박을 믿지 않고, 팹시에게는 그와 얽힌 과거의 상처가 남아 있어 준비 과정부터 감정이 부딪친다. 현금을 먼저 챙기려는 예니콜과 한국 팀을 의심하는 첸까지 가세하면서, 같은 보석을 노리는 열 명의 계산은 제각각 어긋난다. 치밀하게 짠 계획이 실행되기 전부터, 도둑들은 이미 함께 일할 동료의 속내를 훔쳐보기 시작한다.', poster_url = 'https://image.tmdb.org/t/p/w500/tB26UY6RBbv7zs5MVzbcL2gpbJO.jpg', source = 'TMDB', external_id = '124157' WHERE work_id = 16 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 135 WHERE work_id = 16;
-- 17 암살
UPDATE work SET release_date = '2015-07-22', summary = '1933년 일제강점기, 대한민국 임시정부는 일본 측에 얼굴이 알려지지 않은 세 사람을 골라 비밀 암살 작전을 준비한다. 저격수 안옥윤과 신흥무관학교 출신 속사포, 폭탄 전문가 황덕삼은 각자의 기술을 가진 채 하나의 임무로 모인다. 임시정부 경무국 대장 염석진이 이들을 찾아 나서고, 암살단은 일본군 사령관과 친일파 강인국을 표적으로 삼는다. 적의 영역에서 기회를 잡아야 하는 세 사람에게는, 서로를 믿고 정해진 순간에 움직이는 일이 중요하다. 그러나 거액의 의뢰를 받은 청부 살인업자 하와이 피스톨이 암살단을 뒤쫓으면서, 표적을 노리는 자들 또한 누군가의 표적이 된다. 누가 자신의 편인지 쉽게 알 수 없는 상황에서, 세 사람은 서로 다른 목적의 총구 사이로 작전을 이어간다.', poster_url = 'https://image.tmdb.org/t/p/w500/2gAym5Oig2NM7k6iu0DwXlxYaGY.jpg', source = 'TMDB', external_id = '291549' WHERE work_id = 17 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 139 WHERE work_id = 17;
-- 18 건축학개론
UPDATE work SET release_date = '2012-03-22', summary = '생기 넘치지만 숫기 없던 스무 살, 건축학과 승민은 건축학개론 수업에서 처음 만난 음대생 서연에게 반한다. 함께 숙제를 하게 되면서 차츰 마음을 열고 친해지지만, 자신의 마음을 표현하는 데 서툰 순진한 승민은 입 밖에 낼 수 없었던 고백을 마음 속에 품은 채 작은 오해로 인해 서연과 멀어지게 된다. 서른 다섯의 건축가가 된 승민 앞에 15년 만에 불쑥 나타난 서연. 당황스러움을 감추지 못하는 승민에게 서연은 자신을 위한 집을 설계해달라고 한다. 자신의 이름을 건 첫 작품으로 서연의 집을 짓게 된 승민, 함께 집을 완성해 가는 동안 어쩌면 사랑이었을지 모를 그때의 기억이 되살아나 두 사람 사이에 새로운 감정이 쌓이기 시작하는데...', poster_url = 'https://image.tmdb.org/t/p/w500/6ChBkMt5fwYPtKvbtq8irLfuKHc.jpg', source = 'TMDB', external_id = '107235' WHERE work_id = 18 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 118 WHERE work_id = 18;
-- 19 아바타
UPDATE work SET release_date = '2009-12-16', summary = '가까운 미래, 지구는 에너지 고갈 문제를 해결하기 위해 머나먼 행성 판도라에서 대체 자원을 채굴하기 시작한다. 하지만 판도라의 독성을 지닌 대기로 인해 자원 획득에 어려움을 겪게 된 인류는 판도라의 토착민 나비의 외형에 인간의 의식을 주입, 원격 조종이 가능한 새로운 생명체를 탄생시키는 프로그램을 개발한다. 한편 하반신이 마비된 전직 해병대원 제이크 설리는 아바타 프로그램에 참가할 것을 제안받는다. 그 곳에서 자신의 아바타를 통해 자유롭게 걸을 수 있게 된 제이크는 자원 채굴을 막으려는 나비의 무리에 침투하라는 임무를 부여받는데...', poster_url = 'https://image.tmdb.org/t/p/w500/m5lCha2XcbDowDoYHPc0DTNaCPU.jpg', source = 'TMDB', external_id = '19995' WHERE work_id = 19 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 162 WHERE work_id = 19;
-- 20 인터스텔라
UPDATE work SET release_date = '2014-11-04', summary = '세계 각국의 정부와 경제가 완전히 붕괴된 미래가 다가온다. 지난 20세기에 범한 잘못이 전 세계적인 식량 부족을 불러왔고, NASA도 해체되었다. 나사 소속 우주비행사였던 쿠퍼는 지구에 몰아친 식량난으로 옥수수나 키우며 살고 있다. 거센 황사가 몰아친 어느 날 알 수 없는 힘에 이끌려 딸과 함께 도착한 곳은 인류가 이주할 행성을 찾는 나사의 비밀본부. 이 때 시공간에 불가사의한 틈이 열리고, 이 곳을 탐험해 인류를 구해야 하는 임무를 위해 쿠퍼는 만류하는 딸을 뒤로한 채 우주선에 탑승하는데...', poster_url = 'https://image.tmdb.org/t/p/w500/gDN2NWtHbs8ZWEBQM8Dh5OVXdb4.jpg', source = 'TMDB', external_id = '157336' WHERE work_id = 20 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 169 WHERE work_id = 20;
-- 21 인셉션
UPDATE work SET release_date = '2010-07-15', summary = '타인의 꿈에 들어가 잠재의식 속 비밀을 훔치는 코브는 뛰어난 실력에도 불구하고 과거의 사건 때문에 아이들이 있는 집으로 돌아가지 못한다. 사업가 사이토는 경쟁 기업의 후계자 피셔에게 회사를 해체하겠다는 생각을 심어 주면, 코브가 귀국할 수 있도록 돕겠다고 제안한다. 남의 생각을 빼내는 것보다 훨씬 어려운 임무를 위해 코브는 설계자와 위장 전문가, 약제사로 팀을 꾸린다. 이들은 피셔가 그 생각을 자신의 결정으로 받아들이도록 꿈속에 또 다른 꿈을 겹겹이 설계한다. 그러나 깊은 잠으로 내려갈수록 목표의 방어 본능뿐 아니라 코브가 숨겨 온 아내의 기억도 작전을 위협한다. 현실로 돌아갈 기회를 잡으려는 코브에게 가장 통제하기 어려운 침입자는 자신의 마음속에 있다.', poster_url = 'https://image.tmdb.org/t/p/w500/atSxEGstxXRoSKDQFBgqQ5lpGSt.jpg', source = 'TMDB', external_id = '27205' WHERE work_id = 21 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 148 WHERE work_id = 21;
-- 22 다크 나이트
UPDATE work SET release_date = '2008-07-16', summary = '범죄와 부정부패를 제거하여 고담시를 지키려는 배트맨. 그는 짐 고든 형사와 패기 넘치는 고담시 지방 검사 하비 덴트와 함께 도시를 범죄 조직으로부터 영원히 구원하고자 한다. 세 명의 의기투합으로 위기에 처한 악당들이 모인 자리에 보라색 양복을 입고 얼굴에 짙게 화장을 한 괴이한 존재가 나타나 배트맨을 죽이자는 사상 초유의 제안을 한다. 그는 바로 어떠한 룰도, 목적도 없는 사상 최악의 악당 미치광이 살인광대 조커. 배트맨을 죽이고 고담시를 끝장내버리기 위한 조커의 광기 어린 행각에 도시는 혼란에 빠지는데...', poster_url = 'https://image.tmdb.org/t/p/w500/xQPgyZOBhaz1GdCQIPf5A5VeFzO.jpg', source = 'TMDB', external_id = '155' WHERE work_id = 22 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 152 WHERE work_id = 22;
-- 23 타이타닉
UPDATE work SET release_date = '1997-12-18', summary = '1912년 첫 항해를 떠나는 여객선 타이타닉에 화가 잭은 카드 게임으로 얻은 삼등실 표를 들고 오른다. 일등실의 로즈는 부유한 약혼자와 어머니 곁에 있지만, 집안의 형편을 위해 정해진 결혼과 숨 막히는 예절 속에서 자신의 삶을 잃어가고 있다. 절망에 빠진 로즈를 잭이 돕는 일을 계기로, 마주칠 이유조차 없던 두 사람은 서로의 세계에 발을 들인다. 잭의 자유로운 태도와 진심에 마음이 움직인 로즈는 처음으로 자신이 원하는 미래를 그려본다. 신분의 차이와 주변의 반대가 두 사람을 갈라놓으려는 동안, 화려한 선상 생활 아래로 대서양의 차가운 밤이 다가온다. 모두가 안전하리라 믿었던 배 안에서 잭과 로즈는 정해진 삶과 막 시작된 사랑 사이에서 선택해야 한다.', poster_url = 'https://image.tmdb.org/t/p/w500/132KjhVrWUqKFVfMAKKNkherytA.jpg', source = 'TMDB', external_id = '597' WHERE work_id = 23 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 194 WHERE work_id = 23;
-- 24 매트릭스
UPDATE work SET release_date = '1999-03-31', summary = '인간의 기억마저 AI에 의해 입력되고 삭제되는 세상, 진짜보다 더 진짜 같은 가상 현실 매트릭스. 그 속에서 진정한 현실을 인식할 수 없게 재배되는 인간들. 그 매트릭스를 빠져 나오면서 AI에게 가장 위험한 인물이 된 모피어스는 몇 안 되는 동료들과 함께 기계와의 전쟁 전후의의 폐허를 떠돌며 인류를 구할 마지막 영웅을 찾아 헤맨다. 마침내 모피어스는 낮에는 평범한 회사원 앤더슨으로, 밤에는 해커로 활동하는 청년 네오를 그로 지목한다. 모피어스의 지시대로 그를 만나게 된 네오는 두개의 알약 중 하나를 고르는 선택을 하게 되는데...', poster_url = 'https://image.tmdb.org/t/p/w500/fxBxoXFAYKWde6lKzXxSusn18Av.jpg', source = 'TMDB', external_id = '603' WHERE work_id = 24 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 136 WHERE work_id = 24;
-- 25 쥬라기 공원
UPDATE work SET release_date = '1993-06-11', summary = '코스타리카 서해안의 한 섬에 세워진 테마 파크 쥬라기 공원. 최신 복제 기술로 살아난 공룡들이 활보하는 이곳에 공룡학자를 비롯한 각계의 전문가가 일반 공개에 앞서 정밀 안전 진단을 위한 투어에 나선다. 그러나 최첨단 컴퓨터 시스템으로 철저히 통제되고 있는 공룡들이 예기치 않은 사고로 폭주하게 되고, 여기에 설상가상으로 태풍까지 몰려오면서 일행들의 투어는 순식간에 생존을 위한 사투의 현장으로 뒤바뀌는데...', poster_url = 'https://image.tmdb.org/t/p/w500/m4T4h20ame5nG7a5iNPCwk0pHEG.jpg', source = 'TMDB', external_id = '329' WHERE work_id = 25 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 127 WHERE work_id = 25;
-- 26 포레스트 검프
UPDATE work SET release_date = '1994-06-23', summary = '불편한 다리, 남들보다 조금 떨어지는 지능을 가진 포레스트 검프는 헌신적인 어머니의 보살핌과 첫사랑 제니와의 만남으로 편견과 괴롭힘 속에서도 따뜻한 마음을 지니고 성장한다. 또래들의 괴롭힘을 피해 도망치던 포레스트는 누구보다 빠르게 달릴 수 있는 자신의 재능을 깨닫는다. 그의 재능을 알아 본 대학에서 그를 미식축구 선수로 발탁하고, 졸업 후에도 뛰어난 신체능력으로 군에 들어가 무공훈장을 수여받는 등 탄탄한 인생 가도에 오르게 된 포레스트. 하지만 어머니가 병에 걸려 죽음을 맞이하고, 첫사랑 제니 역시 그의 곁을 떠나가며 다시 한 번 인생의 전환점을 맞이하게 되는데...', poster_url = 'https://image.tmdb.org/t/p/w500/l8wvbQQIBV8LBHmx3Wx9xbE2kzt.jpg', source = 'TMDB', external_id = '13' WHERE work_id = 26 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 142 WHERE work_id = 26;
-- 27 라이프 오브 파이
UPDATE work SET release_date = '2012-11-20', summary = '인도에서 동물원을 운영하던 ‘파이’의 가족들은 정부의 지원이 끊기자 캐나다로 이민을 준비한다.  동물들을 싣고 캐나다로 떠나는 배에 탑승한 가족들.  하지만 상상치 못한 폭풍우에 화물선은 침몰하고 가까스로 구명선에 탄 파이만 목숨을 건지게 된다.  구명 보트에는 다리를 다친 얼룩말과 굶주린 하이에나,  그리고 바나나 뭉치를 타고 구명보트로 뛰어든 오랑우탄이 함께 탑승해 긴장감이 감돈다.  하지만 이들 모두를 놀라게 만든 진짜 주인공은  보트 아래에 몸을 숨기고 있었던 벵갈 호랑이 ‘리처드 파커’!  시간이 갈수록 배고픔에 허덕이는 동물들은 서로를 공격하고  결국 리처드 파커와 파이만이 배에 남게 된다.  파이는 배에서 발견한 생존 지침서를 바탕으로  점차 ‘리처드 파커’와 함께 바다 위에서 살아가는 법을 습득하게 된다.  그리고 태평양 한가운데서..  집채 만한 고래와 빛을 내는 해파리, 하늘을 나는 물고기, 그리고 미어캣이 사는 신비의 섬 등  그 누구도 보지 않고서는 믿을 수 없는 놀라운 사건들을 겪게 되는데…!', poster_url = 'https://image.tmdb.org/t/p/w500/mexaTje46m4uJ9YToFUydtZuqaL.jpg', source = 'TMDB', external_id = '87827' WHERE work_id = 27 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 127 WHERE work_id = 27;
-- 28 센과 치히로의 행방불명
UPDATE work SET release_date = '2001-07-20', summary = '평범한 열 살 짜리 소녀 치히로 식구는 이사 가던 중 길을 잘못 들어 낡은 터널을 지나가게 된다. 터널 저편엔 폐허가 된 놀이공원이 있었고 그곳엔 이상한 기운이 흘렀다. 인기척 하나 없는 이 마을의 낯선 분위기에 불길한 기운을 느낀 치히로는 부모님에게 돌아가자고 조르지만 부모님은 호기심에 들떠 마을 곳곳을 돌아다니기 시작한다. 어느 음식점에 도착한 치히로의 부모님은 그 곳에 차려진 음식들을 보고 즐거워하며 허겁지겁 먹어대다가 돼지로 변해버린다. 겁에 질려 당황하는 치히로에게 낯선 소년 하쿠가 나타나 빨리 이곳을 나가라고 소리치는데...', poster_url = 'https://image.tmdb.org/t/p/w500/u1L4qxIu5sC2P082uMHYt7Ifvnj.jpg', source = 'TMDB', external_id = '129' WHERE work_id = 28 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 124 WHERE work_id = 28;
-- 29 하울의 움직이는 성
UPDATE work SET release_date = '2004-09-09', summary = '19세기 말 마법과 과학이 공존하고 있는 세계 앵거리. 소피는 돌아가신 아버지의 모자상점에서 쉴틈없이 일하는 18살 소녀이다. 어느 날 오랫만에 마을로 나간 소피는 우연히 하울을 만나게 된다. 하울은 왕실 마법사로서 핸섬하지만 조금 겁이 많은 청년이다. 황무지 마녀는 두 사람의 사이를 오해, 주문을 걸어 소피를 90살의 늙은 할머니로 만들어 버린다. 가족을 걱정한 소피는 집을 나오게 되고 황무지를 헤매다가 하울이 사는 성에서 가정부로 낯선 생활을 시작한다. 4개의 다리로 걷는 기괴한 움직이는 성 안에서 하울과 소피의 기묘한 사랑과 모험이 시작되는데...', poster_url = 'https://image.tmdb.org/t/p/w500/xiz6TiSduvR1U3VLfWVlBEdT9fO.jpg', source = 'TMDB', external_id = '4935' WHERE work_id = 29 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 119 WHERE work_id = 29;
-- 30 이웃집 토토로
UPDATE work SET release_date = '1988-04-16', summary = '1955년 일본의 아름다운 시골 마을, 상냥하고 의젓한 11살 사츠키와 장난꾸러기에 호기심 많은 4살의 메이 는 사이좋은 자매로 아빠와 함께 도시를 떠나 시골로 이사온다. 자상한 아빠 쿠사카베타츠오는 도쿄에서 대학 연구원이며, 입원 중이지만 따뜻한 미소를 잃지 않는 엄마가 있다. 곧 퇴원하실 엄마를 공기가 맑은 곳에서 맞이하기 위해서다. 숲 한복판에 금방이라도 쓰러질 것처럼 낡은 집을 보며 자매는 새로운 환경에 대한 호기심으로 잔뜩 들뜬다. 그러던 어느 날 사츠키가 학교에 간 동안 메이는 숲에서 정령을 만나다. 메이는 그 정령에게 토토로란 이름을 붙여주는데...', poster_url = 'https://image.tmdb.org/t/p/w500/c9zCkL0rTkNQ1HB9cmeFIqbkS50.jpg', source = 'TMDB', external_id = '8392' WHERE work_id = 30 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 87 WHERE work_id = 30;
-- 31 너의 이름은.
UPDATE work SET release_date = '2016-07-01', summary = '산골 마을의 생활을 답답해하는 소녀 미츠하는 어느 날 잠에서 깨어나 도쿄의 남학생 타키가 되어 있는 자신을 발견한다. 타키도 낯선 집과 가족들 사이에서 미츠하의 몸으로 하루를 보내며 꿈이라고 하기에는 너무 생생한 혼란을 겪는다. 두 사람은 반복되는 몸의 교환을 알아차리고, 서로의 일상을 망치지 않도록 규칙을 정해 메모를 남기기 시작한다. 친구 관계와 학교생활에 뜻밖의 변화가 생기는 동안, 얼굴을 마주한 적 없는 상대가 점점 가까운 존재로 느껴진다. 그러나 어느 순간 교환이 멈추고 연락마저 닿지 않자, 타키는 희미해지는 기억을 붙잡고 미츠하가 사는 곳을 찾아 나선다. 이름과 풍경의 조각만 남은 낯선 소녀를 만나기 위해 그는 지도에서도 확신할 수 없는 길을 더듬는다.', poster_url = 'https://image.tmdb.org/t/p/w500/2DJCufz3Oa703PbLjNX1pM6MCG2.jpg', source = 'TMDB', external_id = '372058' WHERE work_id = 31 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 106 WHERE work_id = 31;
-- 32 토이 스토리
UPDATE work SET release_date = '1995-11-22', summary = '카우보이 인형 우디는 꼬마 주인인 앤디의 가장 사랑받는 장난감이다. 그러나 어느날 버즈라는 새로운 장난감이 등장한다.  버즈는 최신형 장난감으로 레이저 빔 등의 첨단장비를 갖추고 있으나, 버즈는 자신이 장난감임을 인식하지 못하고 자신이 우주에서 온 전사이며 자신이 갖춘 장비로 하늘을 날 수 있다고 믿고 있다. 버즈의 허상을 상처받지 않고 인식시켜 주려는 우디와 친구들. 그러나 뜻밖의 사고가 일어난다. 옆집 개에게 버즈가 납치당하고 이런 버즈를 구하기 위해 우디와 친구들은 구조대를 조직해 버즈를 구하러 가는데...', poster_url = 'https://image.tmdb.org/t/p/w500/iJbm78MxPjLutWUYixbeege8EVe.jpg', source = 'TMDB', external_id = '862' WHERE work_id = 32 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 81 WHERE work_id = 32;
-- 33 겨울왕국
UPDATE work SET release_date = '2013-11-20', summary = '서로가 최고의 친구였던 자매 엘사와 안나. 하지만 언니 엘사에게는 하나뿐인 동생에게조차 말 못할 비밀이 있다. 모든 것을 얼려버리는 신비로운 힘이 바로 그 것. 엘사는 통제할 수 없는 자신의 힘이 두려워 왕국을 떠나 북쪽 산으로 도망치고, 홀로 그곳에서 얼음 궁전을 지어 지내게 된다. 한편 얼어버린 왕국의 저주를 풀기 위해 안나는 언니를 찾아 나서고, 그 과정에서 안나는 까칠한 매력의 얼음장수 크리스토프와 만나 영원히 얼어붙은 왕국을 구해내기 위해 함께 고군분투한다.', poster_url = 'https://image.tmdb.org/t/p/w500/lYceQKe3EsYn7VdDXGTy7dSwBLp.jpg', source = 'TMDB', external_id = '109445' WHERE work_id = 33 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 108 WHERE work_id = 33;
-- 34 라라랜드
UPDATE work SET release_date = '2016-12-01', summary = '차들로 빽빽이 들어찬 LA의 고속도로. 거북이 걸음이던 도로가 뚫리기 시작하지만 미아 지금 손에 든 연기 오디션 대본을 놓지 못한다. 세바스찬은 경적을 누르며 미아를 노려보고는 사라진다. 악연의 시작. 이후 미아는 감미로운 피아노 선율에 이끌려 재즈바로 향하는데, 연주자가 바로 세바스찬이다. 미아가 세바스찬에게 인사를 건네려고 다가가던 순간은 하필 그가 재즈바의 레퍼토리를 무시하고 연주한 탓에 해고된 직후. 세바스찬은 인사를 건네려 다가온 미아를 쌩하니 스쳐 지나가버린다. 세 번째로 두 사람은 파티에 온 손님과 출장 밴드의 키보드 연주자로 다시 만나는데...', poster_url = 'https://image.tmdb.org/t/p/w500/ad9ndytwOckyShSc0A6tx1rZRkW.jpg', source = 'TMDB', external_id = '313369' WHERE work_id = 34 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 127 WHERE work_id = 34;
-- 35 위플래쉬
UPDATE work SET release_date = '2014-10-10', summary = '최고의 드러머가 되기 위해서라면 무엇이든 할 각오가 되어있는 음악대학 신입생 앤드류는 아무도 없는 지하실에서 혼자 드럼을 연습하던 중 우연한 기회로 누구든지 성공으로 이끄는 최고의 실력자이지만, 또한 동시에 최악의 폭군인 플렛처 교수에게 발탁되어 그의 밴드에 들어가게 된다. 설레는 마음으로 참가한 첫 연습에서, 그는 플렛처 교수의 진짜 얼굴을 마주치게 된다. 폭언과 학대 속에 좌절과 성취를 동시에 안겨주는 플렛처의 지독한 교육방식은 천재가 되길 갈망하는 앤드류의 집착을 끌어내며 그를 점점 광기로 몰아넣는데...', poster_url = 'https://image.tmdb.org/t/p/w500/oKNkhmyIBiDENivK6ELZxfBWa3q.jpg', source = 'TMDB', external_id = '244786' WHERE work_id = 35 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 106 WHERE work_id = 35;
-- 36 조커
UPDATE work SET release_date = '2019-10-01', summary = '불황과 범죄로 지친 고담시에서 아서 플렉은 광대 일을 하며 병든 어머니를 돌보고, 언젠가 코미디언이 되겠다는 꿈을 품는다. 하지만 뜻과 상관없이 터져 나오는 웃음은 사람들의 오해를 부르고, 거리에서 당하는 폭력과 일터의 냉대는 그를 더욱 고립시킨다. 상담과 약에 의지하는 아서는 텔레비전 속 진행자처럼 사람들에게 인정받는 무대를 상상한다. 자신을 지탱하던 생활이 조금씩 무너지는 가운데, 동료가 건넨 총 한 자루는 모욕을 참아 오던 그의 곁에 위험한 선택지를 남긴다. 거리에 쌓인 분노와 자신의 상처가 맞물리면서 아서는 남들에게 보여 주는 웃음과 자신의 감정 사이의 간격을 견디기 어려워진다. 누군가를 웃기고 싶었던 남자는 자신을 보고 웃는 도시 앞에서 낯선 얼굴을 준비한다.', poster_url = 'https://image.tmdb.org/t/p/w500/6OnFzi7nU6t4j1rmX9QI8EYDWb4.jpg', source = 'TMDB', external_id = '475557' WHERE work_id = 36 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 122 WHERE work_id = 36;
-- 37 오펜하이머
UPDATE work SET release_date = '2023-07-19', summary = '제2차 세계대전이 한창이던 시기, 나치 독일보다 먼저 원자폭탄을 개발하기 위한 극비 계획 맨해튼 프로젝트가 시작된다. 천재 이론물리학자 J. 로버트 오펜하이머는 레슬리 그로브스 장군에 의해 책임자로 발탁되어, 뉴멕시코의 사막 한가운데 로스앨러모스에 연구소를 세우고 미국 최고의 과학자들을 불러 모은다. 인류 역사를 바꿀 실험을 향해 달려가는 동안 그는 과학자로서의 야망과 도덕적 갈등 사이에서 끊임없이 흔들린다. 그리고 전쟁이 끝난 뒤, 원자력위원회 의장 루이스 스트로스와의 갈등 속에서 그는 자신의 충성심과 과거를 추궁당하는 청문회에 서게 되는데...', poster_url = 'https://image.tmdb.org/t/p/w500/jpD6z9fgNe7OqsHoDeAWQWoULde.jpg', source = 'TMDB', external_id = '872585' WHERE work_id = 37 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 181 WHERE work_id = 37;
-- 38 듄
UPDATE work SET release_date = '2021-09-15', summary = '10191년, 아트레이데스 가문의 후계자인 폴은 시간과 공간을 초월해 과거와 미래를 모두 볼 수 있고, 더 나은 미래를 만들 유일한 구원자인 예지된 자의 운명을 타고났다. 그리고 어떤 계시처럼 매일 꿈에서 아라키스의 행성에 있는 한 여인을 만난다. 귀족들이 지지하는 아트레이데스 가문에 대한 황제의 질투는 폴과 그 일족들을 죽음이 기다리는 아라키스로 이끄는데...', poster_url = 'https://image.tmdb.org/t/p/w500/7zV8FTYofAORGm0Umgh1mNNCym8.jpg', source = 'TMDB', external_id = '438631' WHERE work_id = 38 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 155 WHERE work_id = 38;
-- 39 노트북
UPDATE work SET release_date = '2004-06-25', summary = '17살, 노아는 밝고 순수한 앨리를 보고 첫눈에 반한다. 빠른 속도로 서로에게 빠져드는 둘. 그러나 이들 앞에 놓인 장벽에 막혀 이별하게 된다. 24살, 앨리는 우연히 신문에서 노아의 소식을 접하고 잊을 수 없는 첫사랑 앞에서 다시 한 번 선택의 기로에 서게 되는데…', poster_url = 'https://image.tmdb.org/t/p/w500/ntdgcdsmMuHd9s4oEKTvWDiUyU7.jpg', source = 'TMDB', external_id = '11036' WHERE work_id = 39 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 123 WHERE work_id = 39;
-- 40 파묘
UPDATE work SET release_date = '2024-02-22', summary = '미국 LA, 거액의 의뢰를 받은 무당 화림과 봉길은 기이한 병이 대물림되는 집안의 장손을 만난다. 조상의 묫자리가 화근임을 알아챈 화림은 이장을 권하고, 돈 냄새를 맡은 최고의 풍수사 상덕과 장의사 영근이 합류한다. 절대 사람이 묻힐 수 없는 악지에 자리한 기이한 묘. 상덕은 불길한 기운을 느끼고 제안을 거절하지만, 화림의 설득으로 결국 파묘가 시작되고… 나와서는 안될 것이 나왔다.', poster_url = 'https://image.tmdb.org/t/p/w500/tw0i3kkmOTjDjGFZTLHKhoeXVvA.jpg', source = 'TMDB', external_id = '838209' WHERE work_id = 40 AND type = 'MOVIE' AND source IS NULL;
UPDATE movie SET runtime = 133 WHERE work_id = 40;
