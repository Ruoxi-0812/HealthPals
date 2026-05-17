import Swal from "sweetalert2";

const SWAL_THEME = {
  buttonsStyling: false,
  reverseButtons: true,
  focusConfirm: false,
  customClass: {
    container: "hp-swal",
    popup: "hp-swal__popup",
    title: "hp-swal__title",
    htmlContainer: "hp-swal__text",
    icon: "hp-swal__icon",
    actions: "hp-swal__actions",
    confirmButton: "hp-swal__btn hp-swal__btn--primary",
    cancelButton: "hp-swal__btn hp-swal__btn--ghost",
    denyButton: "hp-swal__btn hp-swal__btn--ghost",
    input: "hp-swal__input",
    validationMessage: "hp-swal__validation",
  },
};

/** Shared themed instance — use via `this.$swal` */
export const themedSwal = Swal.mixin(SWAL_THEME);

function withDangerConfirm(options) {
  if (options.danger !== true) {
    return options;
  }
  return {
    ...options,
    customClass: {
      ...SWAL_THEME.customClass,
      ...(options.customClass || {}),
      confirmButton: "hp-swal__btn hp-swal__btn--danger",
    },
  };
}

/**
 * Auto-closing alert (success / error / info). Uses the same HealthPals theme as $swal.
 */
function swalToast(options = {}) {
  const icon = options.icon || options.type || "success";
  const { type: _ignored, ...rest } = options;
  return themedSwal.fire({
    showConfirmButton: false,
    showCancelButton: false,
    timer: 2000,
    timerProgressBar: true,
    ...rest,
    icon,
  });
}

const swalPlugin = {
  install(Vue) {
    Vue.prototype.$swal = themedSwal;
    Vue.prototype.$swalToast = function (options) {
      return swalToast(options);
    };

    Vue.prototype.$swalConfirm = async function (options = {}) {
      const merged = withDangerConfirm({
        title: "Are you sure?",
        text: "",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel",
        ...options,
      });

      try {
        const result = await themedSwal.fire(merged);
        return result.isConfirmed;
      } catch (error) {
        console.error("Swal Error:", error);
        return false;
      }
    };

    Vue.prototype.$swalLogout = async function () {
      try {
        const result = await themedSwal.fire({
          title: "Log out?",
          text: "You’ll need to sign in again to access your records, messages, and health tools.",
          icon: "question",
          showCancelButton: true,
          confirmButtonText: "Log out",
          cancelButtonText: "Stay signed in",
        });
        return result.isConfirmed;
      } catch (error) {
        console.error("Swal Error:", error);
        return false;
      }
    };
  },
};

export default swalPlugin;
