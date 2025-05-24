package com.common.control.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.common.control.interfaces.PurchaseCallback;
import com.common.control.model.PurchaseModel;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseManager {
    private static PurchaseManager instance;
    private final List<Purchase> purchaseList = new ArrayList<>();
    private List<ProductDetails> productDetailsList;
    private PurchaseCallback callback;
    private BillingClient billingClient;
    private final AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
        queryPurchase();
    };
    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, list) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            for (int i = 0; i < list.size(); i++) {
                handlePurchase(list.get(i));
            }
            callback.purchaseSuccess();

        } else {
            callback.purchaseFail();
        }
    };
    private List<PurchaseModel> purchaseModelList;

    private PurchaseManager() {

    }

    public static PurchaseManager getInstance() {
        if (instance == null) {
            instance = new PurchaseManager();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String convertBillingPeriodToDays(String inputBillingPeriod) {
        Period period = Period.parse(inputBillingPeriod);
        int days = period.getDays();
        return days + " Days";
    }

    public void setCallback(PurchaseCallback callback) {
        this.callback = callback;
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    public void init(Context context, List<PurchaseModel> purchaseModelList) {
        this.purchaseModelList = purchaseModelList;
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        connectGooglePlay();
    }

    private void connectGooglePlay() {
        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryPurchase();
                    queryProductDetails();
                }
            }


            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        for (int i = 0; i < this.purchaseModelList.size(); i++) {
            QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(this.purchaseModelList.get(i).getProductId())
                    .setProductType(this.purchaseModelList.get(i).getType())
                    .build();
            productList.add(product);
        }
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    // check billingResult
                    // process returned productDetailsList
                    PurchaseManager.this.productDetailsList = productDetailsList;
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getBillingPeriod(String productId) {
        if (productDetailsList != null) {
            for (int i = 0; i < productDetailsList.size(); i++) {
                if (productDetailsList.get(i).getProductId().equals(productId)) {
                    {
                        String days = convertBillingPeriodToDays(productDetailsList.get(i).getSubscriptionOfferDetails().get(i).getPricingPhases().getPricingPhaseList().get(i).getBillingPeriod());
                        return days;
                    }
                }
            }
        }

        return "";
    }

    private void queryPurchase() {
        purchaseList.clear();
        QueryPurchasesParams param = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build();
        billingClient.queryPurchasesAsync(param, (billingResult, list) -> purchaseList.addAll(list));

        param = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();
        billingClient.queryPurchasesAsync(param, (billingResult, list) -> purchaseList.addAll(list));
    }


    public void consume(String productId) {
        Purchase purchase = getPurchase(productId);
        if (purchase != null) {
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            ConsumeResponseListener listener = new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, @NonNull String purchaseToken) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
                        Log.d("android_log", "onConsumeResponse: OK");
                        queryPurchase();
                    } else {
                        Log.d("android_log", "onConsumeResponse: Failed");
                    }
                }
            };

            billingClient.consumeAsync(consumeParams, listener);
        }
    }

    private Purchase getPurchase(String productId) {
        for (int i = 0; i < purchaseList.size(); i++) {
            try {
                if (purchaseList.get(i).getProducts().get(0).equals(productId)) {
                    return purchaseList.get(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isPurchased() {
        if (!AdmobManager.getInstance().isHasAds()) {
            return true;
        }
        for (int i = 0; i < purchaseList.size(); i++) {
            Purchase purchase = purchaseList.get(i);
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                return true;
            }
        }
        return false;
    }

    public void launchPurchase(Activity activity, String productId) {
        ProductDetails productDetails = getProductDetail(productId);
        if (productDetails == null) {
            callback.purchaseFail();
            queryPurchase();
            queryProductDetails();
            return;
        }
        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails).setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                // for a list of offers that are available to the user
                .build();
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(productDetailsParams))
                .build();
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        Log.d("android_log", "launchPurchase: " + billingResult.getDebugMessage());

    }

    private ProductDetails getProductDetail(String productId) {
        if (productDetailsList != null) {
            for (int i = 0; i < productDetailsList.size(); i++) {
                if (productDetailsList.get(i).getProductId().equals(productId)) {
                    {
                        return productDetailsList.get(i);
                    }
                }
            }
        }
        return null;
    }

    public String getPriceSub(String productId) {
        if (productDetailsList != null) {
            for (int i = 0; i < productDetailsList.size(); i++) {
                if (productDetailsList.get(i).getProductId().equals(productId)) {
                    {
                        String detail = productDetailsList.get(i).getSubscriptionOfferDetails().get(i).getPricingPhases().getPricingPhaseList().get(i).getFormattedPrice();
                        return detail;
                    }
                }
            }
        }
        return "";
    }

    public String getPriceInApp(String productId) {
        if (productDetailsList != null) {
            for (int i = 0; i < productDetailsList.size(); i++) {
                if (productDetailsList.get(i).getProductId().equals(productId)) {
                    {
                        ProductDetails.OneTimePurchaseOfferDetails detail = productDetailsList.get(i).getOneTimePurchaseOfferDetails();
                        if (detail != null) {
                            return detail.getFormattedPrice();
                        }
                    }
                }
            }
        }
        return "";
    }
}
