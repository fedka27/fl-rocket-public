package wash.rocket.xor.rocketwash.ui;

public interface IFragmentCallbacksInterface
{
	void onLogged();
	void onLoading();
	void onErrorLoading();

	void onGPSWarningDone();
	void onNetworkWarningDone();
}
