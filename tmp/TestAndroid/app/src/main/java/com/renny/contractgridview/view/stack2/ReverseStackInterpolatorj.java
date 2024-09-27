package com.renny.contractgridview.view.stack2;

import android.graphics.Path;
import android.os.Build.VERSION;
import android.view.View;
import android.view.animation.LinearInterpolator;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

public class ReverseStackInterpolatorj extends LinearInterpolator {
    @NotNull
    private final FreePathInterpolator path;

    public ReverseStackInterpolatorj() {
        Path var1 = new Path();
        Path $this$path_u24lambda_u240 = var1;
        //int var3 = false;
        $this$path_u24lambda_u240.moveTo(0.0F, 0.9F);
        $this$path_u24lambda_u240.lineTo(1.0F, 0.95F);
        $this$path_u24lambda_u240.lineTo(2.0F, 3.0F);
        Unit var5 = Unit.INSTANCE;
        this.path = new FreePathInterpolator(var1);
    }

    public float getInterpolation(float input) {
        return Math.max(RangesKt.coerceAtLeast(0.95F + input * 0.05F, 0.95F), input);
    }

    @Metadata(
            mv = {1, 7, 0},
            k = 1,
            xi = 48,
            d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fR\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0010"},
            d2 = {"Lcom/renny/contractgridview/view/stack2/ReverseStackInterpolatorj$ReverseStackTransformer;", "", "()V", "maxTranslationZ", "", "getMaxTranslationZ", "()F", "maxTranslationZ$delegate", "Lkotlin/Lazy;", "transform", "", "x", "v", "Landroid/view/View;", "stackLayoutManager", "Lcom/renny/contractgridview/view/stack2/StackLayoutManager;", "app_debug"}
    )
    public static final class ReverseStackTransformer {
        @NotNull
        public static final ReverseStackTransformer INSTANCE = new ReverseStackTransformer();

        private static  Lazy maxTranslationZ$delegate;/////?

        private ReverseStackTransformer() {
        }

        private  float getMaxTranslationZ() {
            Lazy var1 = maxTranslationZ$delegate;
            Object var2 = null;
            return ((Number)var1.getValue()).floatValue();
        }

        public final void transform(float x, @NotNull View v, @NotNull StackLayoutManager stackLayoutManager) {
            Intrinsics.checkNotNullParameter(v, "v");
            Intrinsics.checkNotNullParameter(stackLayoutManager, "stackLayoutManager");
            if (VERSION.SDK_INT >= 21) {
                v.setElevation(this.getMaxTranslationZ());
                v.setTranslationZ((1.0F - x) * 2.0F);
            }

        }

       /* static {
            maxTranslationZ$delegate = LazyKt.lazy((Function0)null.INSTANCE);
        }*/
    }
}
