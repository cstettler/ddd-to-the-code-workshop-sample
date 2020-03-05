package com.github.cstettler.dddttc.accounting.infrastructure.web;

import com.github.cstettler.dddttc.accounting.application.WalletService;
import com.github.cstettler.dddttc.accounting.domain.Wallet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

import static com.github.cstettler.dddttc.accounting.infrastructure.web.AccountingController.WalletViewModel.toWalletViewModels;
import static com.github.cstettler.dddttc.support.infrastructure.web.ModelAndViewBuilder.modelAndView;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/accounting")
class AccountingController {

    private final WalletService walletService;

    AccountingController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallets")
    ModelAndView listWallets() {
        List<Wallet> wallets = this.walletService.listWallets().stream()
                .sorted(comparing((wallet) -> wallet.id().value()))
                .collect(toList());

        return modelAndView("list-wallets")
                .property("wallets", toWalletViewModels(wallets))
                .build();
    }


    static class WalletViewModel {

        public final String id;
        public final BigDecimal balance;

        private WalletViewModel(Wallet wallet) {
            this.id = wallet.id().value();
            this.balance = wallet.balance().value();
        }

        static WalletViewModel toWalletViewModel(Wallet wallet) {
            return new WalletViewModel(wallet);
        }

        static List<WalletViewModel> toWalletViewModels(List<Wallet> wallets) {
            return wallets.stream()
                    .map((wallet) -> toWalletViewModel(wallet))
                    .collect(toList());
        }

    }

}
