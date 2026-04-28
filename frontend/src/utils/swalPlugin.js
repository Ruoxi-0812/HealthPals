// swalPlugin.js
import Swal from "sweetalert2";

const swalPlugin = {
  install(Vue) {
    Vue.prototype.$swalConfirm = async function (options = {}) {
      const defaultOptions = {
        title: "hint",
        text: "",
        icon: "info",
        reverseButtons: true,
        showCancelButton: true,
        confirmButtonText: "confirm",
        cancelButtonText: "cancel",
        customClass: {
          confirmButton: "sweet-btn-primary",
        },
        ...options,
      };

      try {
        const result = await Swal.fire(defaultOptions);
        return result.isConfirmed;
      } catch (error) {
        console.error("Swal Error:", error);
        return false;
      }
    };
  },
};

export default swalPlugin;
