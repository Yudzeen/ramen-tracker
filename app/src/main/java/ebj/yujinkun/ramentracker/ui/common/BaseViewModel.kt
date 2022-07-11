package ebj.yujinkun.ramentracker.ui.common;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends ViewModel {

    private CompositeDisposable disposables = new CompositeDisposable();

    protected void bind(Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }
}
