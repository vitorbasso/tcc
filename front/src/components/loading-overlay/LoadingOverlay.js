import Overlay from "../overlay/Overlay";
import Spinner from "../spinner/Spinner";

function LoadingOverlay() {
  return (
    <Overlay>
      <Spinner />
    </Overlay>
  );
}

export default LoadingOverlay;
