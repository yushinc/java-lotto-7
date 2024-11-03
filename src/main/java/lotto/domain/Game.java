package lotto.domain;

import static lotto.domain.Lotto.LOTTO_NUMBER_RANGE_REGEX;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final int LOTTO_MAX_COUNT = 100;
    private static final int[] PRIZE_AMOUNTS = {5000, 50000, 1500000, 30000000, 2000000000};

    private final List<Lotto> lottos;
    private Lotto winningLotto;
    private int bonusNumber;
    private List<Lotto> winningLottos;

    public Game(List<Lotto> lottos, Lotto winningLotto, int bonusNumber) {
        validateLottoMaxCount(lottos);
        validateRange(bonusNumber);
        validateDuplicate(winningLotto, bonusNumber);

        this.lottos = lottos;
        this.winningLotto = winningLotto;
        this.bonusNumber = bonusNumber;
        this.winningLottos = new ArrayList<>();
    }

    public Game(List<Lotto> lottos) {
        validateLottoMaxCount(lottos);
        this.lottos = lottos;
    }

    // 당첨 결과 비교
    public int[] compareNumbers(Lotto winningLotto) {
        int[] results = new int[5];

        for (Lotto lotto : lottos) {
            int correctCount = (int) lotto.getNumbers().stream()
                    .filter(winningLotto.getNumbers()::contains)
                    .count();

            updateResults(results, correctCount, lotto);
        }

        return results;
    }

    private void updateResults(int[] results, int correctCount, Lotto lotto) {
        switch (correctCount) {
            case 3 -> results[0]++;
            case 4 -> results[1]++;
            case 5 -> checkBonusNumberMatch(results, lotto);
            case 6 -> results[4]++;
        }
        winningLottos.add(lotto);
    }

    private void checkBonusNumberMatch(int[] results, Lotto lotto) {
        if (lotto.getNumbers().contains(bonusNumber)) {
            results[2]++;
            return;
        }
        results[3]++;
    }

    // 수익률 계산
    public long calculateRateOfReturn(int purchaseAmount, int[] results) {
        int revenue = 0;

        for (int i = 0; i < results.length; i++) {
            revenue += results[i] * PRIZE_AMOUNTS[i];
        }

        return (long) revenue * 100 / purchaseAmount;
    }

    private void validateLottoMaxCount(List<Lotto> lottos) {
        if (lottos.size() > LOTTO_MAX_COUNT) {
            throw new IllegalArgumentException("[ERROR] 로또는 최대 100개까지만 발행할 수 있습니다.");
        }
    }

    public void validateRange(int bonusNumber) {
        Integer parsedNumber = bonusNumber;
        if (!parsedNumber.toString().matches(LOTTO_NUMBER_RANGE_REGEX)) {
            throw new IllegalArgumentException("[ERROR] 숫자의 범위는 1~45까지 입니다.");
        }
    }

    public void validateDuplicate(Lotto winningLotto, int bonusNumber) {
        boolean hasDuplicate = winningLotto.getNumbers().stream()
                .anyMatch(number -> number == bonusNumber);

        if (hasDuplicate) {
            throw new IllegalArgumentException("[ERROR] 보너스 번호는 당첨 번호와 중복될 수 없습니다.");
        }
    }
}
