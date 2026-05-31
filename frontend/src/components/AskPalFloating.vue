<template>
  <div class="ask-pal-floating" :class="{ 'is-open': isOpen }">
    <transition name="ask-pal-panel">
      <section
        v-if="isOpen"
        class="ask-pal-panel"
        aria-label="Ask Pal AI assistant"
      >
        <header class="ask-pal-panel__header">
          <div class="ask-pal-panel__brand">
            <span class="ask-pal-panel__mark" aria-hidden="true">
              <Logo :size="30" flag />
            </span>
            <div>
              <h2>Ask Pal</h2>
              <p>Personal health assistant</p>
            </div>
          </div>
          <button
            type="button"
            class="ask-pal-panel__close"
            aria-label="Close Ask Pal"
            @click="isOpen = false"
          >
            <i class="el-icon-close" aria-hidden="true" />
          </button>
        </header>

        <div ref="threadEl" class="ask-pal-panel__thread">
          <div v-if="!thread.length && !loading" class="ask-pal-panel__empty">
            <p class="ask-pal-panel__eyebrow">How can I help?</p>
            <h3>Ask about sleep, energy, habits, or your health records.</h3>
            <div class="ask-pal-panel__prompts">
              <button
                v-for="(prompt, i) in starterPrompts"
                :key="i"
                type="button"
                :disabled="loading"
                @click="usePrompt(prompt)"
              >
                {{ prompt }}
              </button>
            </div>
          </div>

          <div
            v-for="(turn, idx) in thread"
            :key="'turn-' + idx + '-' + turn.role"
            class="ask-pal-panel__turn"
            :class="'is-' + turn.role"
          >
            <span class="ask-pal-panel__avatar" aria-hidden="true">
              <i
                :class="
                  turn.role === 'user' ? 'el-icon-user' : 'el-icon-magic-stick'
                "
              />
            </span>
            <div class="ask-pal-panel__bubble">{{ turn.text }}</div>
          </div>

          <div v-if="loading" class="ask-pal-panel__turn is-assistant">
            <span class="ask-pal-panel__avatar" aria-hidden="true">
              <i class="el-icon-magic-stick" />
            </span>
            <div class="ask-pal-panel__bubble ask-pal-panel__typing">
              <span />
              <span />
              <span />
            </div>
          </div>
        </div>

        <form class="ask-pal-panel__compose" @submit.prevent="send">
          <textarea
            ref="inputEl"
            v-model="input"
            rows="2"
            placeholder="Ask Pal anything..."
            :disabled="loading"
            @keydown.enter.exact.prevent="send"
          />
          <button
            type="submit"
            :disabled="loading || !input.trim()"
            aria-label="Send"
          >
            <i class="el-icon-s-promotion" aria-hidden="true" />
          </button>
        </form>
      </section>
    </transition>

    <button
      type="button"
      class="ai-fab"
      :class="{ 'is-active': isOpen }"
      aria-label="Open Ask Pal AI assistant"
      title="Ask Pal AI"
      @click="toggle"
    >
      <span class="ai-fab__glow" aria-hidden="true" />
      <span class="ai-fab__logo" aria-hidden="true">
        <Logo :size="34" flag />
      </span>
      <span class="ai-fab__label" aria-hidden="true">Ask Pal</span>
      <span class="ai-fab__badge" aria-hidden="true">AI</span>
    </button>
  </div>
</template>

<script>
import Logo from "@/components/Logo.vue";

export default {
  name: "AskPalFloating",
  components: { Logo },
  data() {
    return {
      isOpen: false,
      input: "",
      thread: [],
      loading: false,
      starterPrompts: [
        "How can I improve my sleep?",
        "What habits help daily energy?",
        "How should I read unusual metrics?",
      ],
    };
  },
  methods: {
    toggle() {
      this.isOpen = !this.isOpen;
      if (this.isOpen) {
        this.focusInput();
      }
    },
    focusInput() {
      this.$nextTick(() => {
        if (this.$refs.inputEl) {
          this.$refs.inputEl.focus();
        }
      });
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.threadEl;
        if (el) {
          el.scrollTop = el.scrollHeight;
        }
      });
    },
    usePrompt(text) {
      this.input = text;
      this.focusInput();
    },
    async send() {
      const text = (this.input || "").trim();
      if (!text || this.loading) {
        return;
      }

      this.thread.push({ role: "user", text });
      this.input = "";
      this.loading = true;
      this.scrollToBottom();

      try {
        const history = this.thread
          .slice(0, -1)
          .map((turn) => ({ role: turn.role, content: turn.text }));
        const response = await this.$axios.post(
          "/health-assistant/chat",
          { message: text, history },
          { timeout: 125000 },
        );
        const { data } = response;
        if (data.code === 200 && data.data && data.data.reply) {
          this.thread.push({ role: "assistant", text: data.data.reply });
          this.scrollToBottom();
        } else {
          this.thread.pop();
          this.input = text;
          this.$message.error(data.msg || "Something went wrong.");
        }
      } catch (error) {
        console.error(error);
        this.thread.pop();
        this.input = text;
        this.$message.error(
          "Could not reach the assistant. Check your connection or try again.",
        );
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.ask-pal-floating {
  position: fixed;
  right: clamp(18px, 3vw, 34px);
  bottom: clamp(18px, 3vw, 34px);
  z-index: 1200;
}

.ask-pal-panel {
  position: absolute;
  right: 0;
  bottom: 86px;
  width: min(520px, calc(100vw - 32px));
  height: min(660px, calc(100vh - 130px));
  min-height: 480px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(126, 197, 160, 0.28);
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.98),
    rgba(247, 252, 249, 0.96)
  );
  box-shadow:
    0 28px 70px rgba(36, 51, 43, 0.18),
    0 10px 26px rgba(42, 157, 111, 0.14);
}

.ask-pal-panel__header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 16px 16px 14px;
  border-bottom: 1px solid rgba(126, 197, 160, 0.18);
  background:
    radial-gradient(
      circle at 12% 0%,
      rgba(126, 197, 160, 0.2),
      transparent 34%
    ),
    rgba(255, 255, 255, 0.78);
}

.ask-pal-panel__brand {
  display: flex;
  align-items: center;
  gap: 11px;
  min-width: 0;

  h2 {
    margin: 0;
    font-family: var(--nb-font-display, Georgia, serif);
    font-size: 22px;
    font-weight: 700;
    line-height: 1.1;
    color: #24332b;
    letter-spacing: 0;
  }

  p {
    margin: 3px 0 0;
    font-size: 12px;
    font-weight: 650;
    color: rgba(36, 51, 43, 0.52);
  }
}

.ask-pal-panel__mark {
  width: 42px;
  height: 42px;
  border: 1px solid rgba(126, 197, 160, 0.34);
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f6fbf8 100%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.9),
    0 6px 14px rgba(25, 91, 61, 0.08);
}

.ask-pal-panel__close {
  width: 34px;
  height: 34px;
  border: 1px solid rgba(126, 197, 160, 0.24);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  color: #4d655b;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: #f3faf6;
  }
}

.ask-pal-panel__thread {
  flex: 1 1 auto;
  min-height: 0;
  overflow-y: auto;
  padding: 18px 16px;
}

.ask-pal-panel__empty {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left;

  h3 {
    margin: 0;
    font-family: var(--nb-font-display, Georgia, serif);
    font-size: 24px;
    line-height: 1.2;
    font-weight: 700;
    color: #24332b;
    letter-spacing: 0;
  }
}

.ask-pal-panel__eyebrow {
  margin: 0 0 9px;
  color: #2a9d6f;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.ask-pal-panel__prompts {
  display: grid;
  gap: 9px;
  margin-top: 20px;

  button {
    cursor: pointer;
    padding: 11px 13px;
    border: 1px solid rgba(126, 197, 160, 0.34);
    border-radius: 14px;
    background: rgba(236, 249, 242, 0.7);
    color: #355247;
    font-family: var(--nb-font);
    font-size: 13px;
    font-weight: 700;
    text-align: left;
  }
}

.ask-pal-panel__turn {
  display: flex;
  align-items: flex-start;
  gap: 9px;
  margin-bottom: 14px;

  &.is-user {
    flex-direction: row-reverse;
  }
}

.ask-pal-panel__avatar {
  flex: 0 0 auto;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(42, 157, 111, 0.14);
  color: #2a9d6f;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
}

.is-user .ask-pal-panel__avatar {
  background: #2a9d6f;
  color: #ffffff;
}

.ask-pal-panel__bubble {
  max-width: calc(100% - 44px);
  padding: 10px 13px;
  border-radius: 16px;
  border-bottom-left-radius: 5px;
  background: #ffffff;
  border: 1px solid rgba(126, 197, 160, 0.22);
  color: #24332b;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
  box-shadow: 0 5px 14px rgba(36, 51, 43, 0.05);
}

.is-user .ask-pal-panel__bubble {
  border: none;
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 5px;
  background: #2a9d6f;
  color: #ffffff;
  box-shadow: 0 5px 14px rgba(42, 157, 111, 0.12);
}

.ask-pal-panel__typing {
  display: inline-flex;
  gap: 5px;
  align-items: center;
  min-width: 52px;

  span {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: rgba(42, 157, 111, 0.55);
    animation: ask-pal-dot 1.2s ease-in-out infinite;
  }

  span:nth-child(2) {
    animation-delay: 0.15s;
  }

  span:nth-child(3) {
    animation-delay: 0.3s;
  }
}

@keyframes ask-pal-dot {
  0%,
  80%,
  100% {
    opacity: 0.35;
    transform: scale(0.85);
  }
  40% {
    opacity: 1;
    transform: scale(1);
  }
}

.ask-pal-panel__compose {
  flex-shrink: 0;
  display: flex;
  gap: 10px;
  padding: 12px;
  border-top: 1px solid rgba(126, 197, 160, 0.18);
  background: rgba(255, 255, 255, 0.82);

  textarea {
    flex: 1 1 auto;
    min-width: 0;
    max-height: 98px;
    resize: none;
    border: 1px solid rgba(126, 197, 160, 0.36);
    border-radius: 16px;
    background: #f8fcf9;
    padding: 11px 12px;
    color: #24332b;
    font-family: var(--nb-font);
    font-size: 14px;
    line-height: 1.45;
    outline: none;

    &:focus {
      border-color: rgba(42, 157, 111, 0.55);
      box-shadow: 0 0 0 3px rgba(42, 157, 111, 0.1);
    }
  }

  button {
    flex: 0 0 auto;
    align-self: flex-end;
    width: 44px;
    height: 44px;
    border: none;
    border-radius: 15px;
    background: #2a9d6f;
    color: #ffffff;
    cursor: pointer;
    font-size: 18px;
    box-shadow: 0 8px 18px rgba(42, 157, 111, 0.22);

    &:disabled {
      cursor: not-allowed;
      opacity: 0.45;
    }
  }
}

.ai-fab {
  position: relative;
  width: 70px;
  height: 70px;
  padding: 0;
  border: 1px solid rgba(126, 197, 160, 0.34);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(10px);
  box-shadow:
    0 18px 40px rgba(42, 157, 111, 0.2),
    0 6px 14px rgba(22, 83, 57, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.92);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  overflow: visible;
  transition:
    transform 0.16s ease,
    box-shadow 0.16s ease,
    filter 0.16s ease;
}

.ai-fab::before {
  content: "";
  position: absolute;
  inset: 8px;
  border-radius: 19px;
  background:
    radial-gradient(
      circle at 34% 24%,
      rgba(255, 255, 255, 0.9),
      transparent 34%
    ),
    linear-gradient(145deg, #8ed8ad 0%, #55c08a 52%, #2a9d6f 100%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.58),
    inset 0 -8px 18px rgba(28, 122, 80, 0.16);
}

.ai-fab:hover,
.ai-fab.is-active {
  transform: translateY(-4px) scale(1.025);
  box-shadow:
    0 22px 48px rgba(42, 157, 111, 0.24),
    0 8px 18px rgba(22, 83, 57, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.96);
  filter: saturate(1.03) brightness(1.01);
}

.ai-fab:focus-visible {
  outline: 3px solid rgba(42, 157, 111, 0.28);
  outline-offset: 4px;
}

.ai-fab__glow {
  position: absolute;
  inset: -18px;
  border-radius: inherit;
  background: radial-gradient(
    circle,
    rgba(126, 197, 160, 0.26),
    rgba(126, 197, 160, 0)
  );
  filter: blur(12px);
  z-index: -1;
  opacity: 0.82;
}

.ai-fab__logo {
  position: relative;
  z-index: 1;
  width: 44px;
  height: 44px;
  border-radius: 15px;
  background: linear-gradient(180deg, #ffffff 0%, #f5fbf7 100%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.82),
    0 6px 14px rgba(25, 91, 61, 0.14);
  transition: transform 0.16s ease;
}

.ai-fab:hover .ai-fab__logo {
  transform: scale(1.04);
}

.ai-fab__label {
  position: absolute;
  right: 76px;
  top: 50%;
  transform: translate(8px, -50%);
  opacity: 0;
  pointer-events: none;
  white-space: nowrap;
  padding: 9px 13px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(126, 197, 160, 0.28);
  color: #2f4a40;
  font-family: var(--nb-font);
  font-size: 13px;
  font-weight: 800;
  line-height: 1;
  box-shadow: 0 12px 28px rgba(36, 51, 43, 0.12);
  transition:
    opacity 0.16s ease,
    transform 0.16s ease;
}

.ai-fab:hover .ai-fab__label,
.ai-fab:focus-visible .ai-fab__label {
  transform: translate(0, -50%);
  opacity: 1;
}

.ai-fab__badge {
  position: absolute;
  z-index: 2;
  top: -7px;
  right: -7px;
  width: 28px;
  height: 28px;
  padding: 0;
  border-radius: 50%;
  border: 2px solid #ffffff;
  background: linear-gradient(135deg, #ff7c98 0%, #ec5f7d 100%);
  color: #ffffff;
  font-family: var(--nb-font);
  font-size: 10px;
  font-weight: 800;
  line-height: 24px;
  text-align: center;
  letter-spacing: 0;
  box-shadow:
    0 5px 12px rgba(212, 50, 83, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.28);
}

.ask-pal-panel-enter-active,
.ask-pal-panel-leave-active {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}

.ask-pal-panel-enter,
.ask-pal-panel-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.98);
}

@media (max-width: 640px) {
  .ask-pal-floating {
    right: 16px;
    bottom: 16px;
  }

  .ask-pal-panel {
    right: -8px;
    bottom: 78px;
    width: calc(100vw - 32px);
    height: min(590px, calc(100vh - 112px));
    min-height: 440px;
    border-radius: 22px;
  }

  .ai-fab {
    width: 64px;
    height: 64px;
    border-radius: 21px;
  }

  .ai-fab::before {
    inset: 7px;
    border-radius: 17px;
  }

  .ai-fab__logo {
    width: 40px;
    height: 40px;
    border-radius: 13px;
  }

  .ai-fab__badge {
    top: -6px;
    right: -6px;
    width: 26px;
    height: 26px;
    font-size: 9px;
    line-height: 22px;
  }

  .ai-fab__label {
    display: none;
  }
}
</style>
