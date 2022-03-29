package com.affable.smartbills.qrcode;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;

import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class ZatcaQRCodeGeneration {
    
    public static final class Builder {
        @NotNull
        private final String SELLER_NAME_TAG = "1";
        @NotNull
        private final String TAX_NUMBER_TAG = "2";
        @NotNull
        private final String INVOICE_DATE_TAG = "3";
        @NotNull
        private final String TOTAL_AMOUNT_TAG = "4";
        @NotNull
        private final String TAX_AMOUNT_TAG = "5";
        @NotNull
        private String sellerName = "";
        @NotNull
        private String taxNumber = "";
        @NotNull
        private String invoiceDate = "";
        @NotNull
        private String totalAmount = "";
        @NotNull
        private String taxAmount = "";

        @NotNull
        public final String getSELLER_NAME_TAG() {
            return this.SELLER_NAME_TAG;
        }

        @NotNull
        public final String getTAX_NUMBER_TAG() {
            return this.TAX_NUMBER_TAG;
        }

        @NotNull
        public final String getINVOICE_DATE_TAG() {
            return this.INVOICE_DATE_TAG;
        }

        @NotNull
        public final String getTOTAL_AMOUNT_TAG() {
            return this.TOTAL_AMOUNT_TAG;
        }

        @NotNull
        public final String getTAX_AMOUNT_TAG() {
            return this.TAX_AMOUNT_TAG;
        }

        @NotNull
        public final String getSellerName() {
            return this.sellerName;
        }

        public final void setSellerName(@NotNull String var1) {
            Intrinsics.checkNotNullParameter(var1, "<set-?>");
            this.sellerName = var1;
        }

        @NotNull
        public final String getTaxNumber() {
            return this.taxNumber;
        }

        public final void setTaxNumber(@NotNull String var1) {
            Intrinsics.checkNotNullParameter(var1, "<set-?>");
            this.taxNumber = var1;
        }

        @NotNull
        public final String getInvoiceDate() {
            return this.invoiceDate;
        }

        public final void setInvoiceDate(@NotNull String var1) {
            Intrinsics.checkNotNullParameter(var1, "<set-?>");
            this.invoiceDate = var1;
        }

        @NotNull
        public final String getTotalAmount() {
            return this.totalAmount;
        }

        public final void setTotalAmount(@NotNull String var1) {
            Intrinsics.checkNotNullParameter(var1, "<set-?>");
            this.totalAmount = var1;
        }

        @NotNull
        public final String getTaxAmount() {
            return this.taxAmount;
        }

        public final void setTaxAmount(@NotNull String var1) {
            Intrinsics.checkNotNullParameter(var1, "<set-?>");
            this.taxAmount = var1;
        }

        @NotNull
        public final ZatcaQRCodeGeneration.Builder sellerName(@Nullable String value) {
            ZatcaQRCodeGeneration.Builder $this$apply = this;
            
            if (value != null) {
                $this$apply.sellerName = value;
            }

            return this;
        }

        @NotNull
        public final ZatcaQRCodeGeneration.Builder taxNumber(@Nullable String value) {
          
            ZatcaQRCodeGeneration.Builder $this$apply = this;
          
            if (value != null) {
                $this$apply.taxNumber = value;
            }

            return this;
        }

        @NotNull
        public final ZatcaQRCodeGeneration.Builder invoiceDate(@Nullable String value) {
           
            ZatcaQRCodeGeneration.Builder $this$apply = this;
     
            if (value != null) {
                $this$apply.invoiceDate = value;
            }

            return this;
        }

        @NotNull
        public final ZatcaQRCodeGeneration.Builder totalAmount(@Nullable String value) {
           
            ZatcaQRCodeGeneration.Builder $this$apply = this;
          
            if (value != null) {
                $this$apply.totalAmount = value;
            }

            return this;
        }


        public final void taxAmount(@Nullable String value) {
            
            ZatcaQRCodeGeneration.Builder $this$apply = this;
            if (value != null) {
                $this$apply.taxAmount = value;
            }

        }

        private byte[] convertTagsAndLengthToHexValues(String tag, String length, String value) {
            byte[] var10000 = new byte[2];
            var10000[0] = Byte.parseByte(tag);
            var10000[1] = Byte.parseByte(length);
            Charset var7 = Charsets.UTF_8;
              if (value == null) {
                throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
            } else {
                byte[] var10001 = value.getBytes(var7);
                Intrinsics.checkNotNullExpressionValue(var10001, "(this as java.lang.String).getBytes(charset)");
                return ArraysKt.plus(var10000, var10001);
            }
        }

        @NotNull
        public final String getBase64() {
            String var10001;
            String var10002;
            byte[] var22;
            Integer var23;
            {
                var10001 = this.SELLER_NAME_TAG;
                var10002 = this.sellerName;
                String var2 = var10002;
                Charset var3 = Charsets.UTF_8;

                var22 = var2.getBytes(var3);
                Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                var23 = var22.length;

            }

            byte[] tlv1;
            {
                tlv1 = this.convertTagsAndLengthToHexValues(var10001, String.valueOf(var23).trim(), this.sellerName);
                var10001 = this.TAX_NUMBER_TAG;
                var10002 = this.taxNumber;
                String var10 = var10002;
                Charset var12 = Charsets.UTF_8;

                var22 = var10.getBytes(var12);
                Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                var23 = var22.length;

            }
            byte[] tlv2;
            {
                tlv2 = this.convertTagsAndLengthToHexValues(var10001, String.valueOf(var23).trim(), this.taxNumber);
                var10001 = this.INVOICE_DATE_TAG;
                var10002 = this.invoiceDate;
                String var13 = var10002;
                Charset var15 = Charsets.UTF_8;

                var22 = var13.getBytes(var15);
                Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                var23 = var22.length;

            }

            byte[] tlv3;
            {
                tlv3 = this.convertTagsAndLengthToHexValues(var10001, String.valueOf(var23).trim(), this.invoiceDate);
                var10001 = this.TOTAL_AMOUNT_TAG;
                var10002 = this.totalAmount;
                String var16 = var10002;
                Charset var18 = Charsets.UTF_8;
                boolean var7 = false;

                var22 = var16.getBytes(var18);
                Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                var23 = var22.length;

            }

            byte[] tlv4;
            {
                tlv4 = this.convertTagsAndLengthToHexValues(var10001, String.valueOf(var23).trim(), this.totalAmount);
                var10001 = this.TAX_AMOUNT_TAG;
                var10002 = this.taxAmount;
                String var19 = var10002;
                Charset var21 = Charsets.UTF_8;
                boolean var8 = false;

                var22 = var19.getBytes(var21);
                Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                var23 = var22.length;

            }

            byte[] tlv5 = this.convertTagsAndLengthToHexValues(var10001, String.valueOf(var23).trim(), this.taxAmount);

            byte[] tlvs = ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(tlv1, tlv2), tlv3), tlv4), tlv5);

            String var10000 = Base64.encodeToString(tlvs, 0);
            Intrinsics.checkNotNullExpressionValue(var10000, "Base64.encodeToString(tlvs, Base64.DEFAULT)");
            return var10000;
        }
    }
}
