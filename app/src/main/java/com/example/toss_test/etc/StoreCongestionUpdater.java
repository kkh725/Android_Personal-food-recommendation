package com.example.toss_test.etc;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

public class StoreCongestionUpdater {
    private Map<String, Integer> storeCongestionMap = new HashMap<>();

    // 가게 이름과 혼잡도 정보 업데이트 메서드
    public void updateStoreCongestion(String storeName, int congestionLevel) {
        storeCongestionMap.put(storeName, congestionLevel);
    }

    // 가게 이름으로 혼잡도 정보 조회 메서드

    /**
     * 즉, storeCongestionMap에서 storeName 키에 해당하는 값이 있다면 해당 값을 반환하고, 없다면 0을 반환합니다.
     * @param storeName
     * @return
     */
    public int getCongestionLevel(String storeName) { //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return storeCongestionMap.getOrDefault(storeName, 0); // 기본값 0 설정
        }
        return 0;
    }
}