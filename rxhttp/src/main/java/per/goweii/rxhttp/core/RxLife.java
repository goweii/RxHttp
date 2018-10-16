package per.goweii.rxhttp.core;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/13
 */
public class RxLife {

    private CompositeDisposable mCompositeDisposable = null;

    private RxLife() {
        mCompositeDisposable = new CompositeDisposable();
    }

    public static RxLife create() {
        return new RxLife();
    }

    public void destroy() {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            return;
        }
        mCompositeDisposable.dispose();
    }

    public void add(Disposable d) {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(d);
    }
}
