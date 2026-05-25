<template>
  <div class="health-ai-page">
    <section class="health-ai-page__surface nb-surface">
      <div ref="threadEl" class="health-ai-page__thread">
        <div
          v-if="!thread.length && !loading"
          class="health-ai-page__welcome"
        >
          <div class="health-ai-page__welcome-icon" aria-hidden="true">
            <i class="el-icon-chat-dot-round" />
          </div>
          <h2 class="health-ai-page__welcome-title">Ask Pal</h2>
          <p class="health-ai-page__welcome-text">
            Your wellness companion — ask about how you feel, healthy habits, or
            what your logged metrics might mean.
          </p>
          <p class="health-ai-page__welcome-label">Try asking</p>
          <div class="health-ai-page__prompts">
            <button
              v-for="(prompt, i) in starterPrompts"
              :key="i"
              type="button"
              class="health-ai-page__prompt"
              :disabled="loading"
              @click="usePrompt(prompt)"
            >
              {{ prompt }}
            </button>
          </div>
        </div>

        <div
          v-for="(turn, idx) in thread"
          :key="'t-' + idx + '-' + turn.role"
          class="health-ai-page__turn"
          :class="'is-' + turn.role"
        >
          <div
            class="health-ai-page__avatar"
            :class="'is-' + turn.role"
            aria-hidden="true"
          >
            <i
              :class="
                turn.role === 'user'
                  ? 'el-icon-user'
                  : 'el-icon-magic-stick'
              "
            />
          </div>
          <div class="health-ai-page__content">
            <span class="health-ai-page__role">{{
              turn.role === "user" ? "You" : "Pal"
            }}</span>
            <div class="health-ai-page__bubble">{{ turn.text }}</div>
          </div>
        </div>

        <div v-if="loading" class="health-ai-page__turn is-assistant is-typing">
          <div class="health-ai-page__avatar is-assistant" aria-hidden="true">
            <i class="el-icon-magic-stick" />
          </div>
          <div class="health-ai-page__content">
            <span class="health-ai-page__role">Pal</span>
            <div class="health-ai-page__bubble health-ai-page__bubble--typing">
              <span class="health-ai-page__dot" />
              <span class="health-ai-page__dot" />
              <span class="health-ai-page__dot" />
            </div>
          </div>
        </div>
      </div>

      <form class="health-ai-page__compose" @submit.prevent="send">
        <el-input
          ref="inputEl"
          v-model="input"
          type="textarea"
          :rows="3"
          resize="none"
          placeholder="Describe how you feel or what you'd like to know…"
          :disabled="loading"
          @keydown.native.enter.exact.prevent="send"
        />
        <div class="health-ai-page__actions">
          <button
            v-if="thread.length"
            type="button"
            class="health-ai-page__btn health-ai-page__btn--ghost"
            :disabled="loading"
            @click="clearChat"
          >
            Clear chat
          </button>
          <button
            type="submit"
            class="health-ai-page__btn health-ai-page__btn--primary"
            :disabled="loading || !input.trim()"
          >
            <i v-if="!loading" class="el-icon-s-promotion" aria-hidden="true" />
            {{ loading ? "Thinking…" : "Send" }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script>
export default {
  name: "HealthAssistant",
  data() {
    return {
      input: "",
      thread: [],
      loading: false,
      starterPrompts: [
        "How can I build a better sleep routine?",
        "What habits help with daily energy?",
        "How should I interpret unusual metric readings?",
      ],
    };
  },
  methods: {
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
      this.$nextTick(() => {
        const ref = this.$refs.inputEl;
        const textarea = ref && ref.$refs ? ref.$refs.textarea : null;
        if (textarea) {
          textarea.focus();
        }
      });
    },
    clearChat() {
      this.thread = [];
      this.input = "";
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
          .map((t) => ({ role: t.role, content: t.text }));
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
      } catch (e) {
        console.error(e);
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
.health-ai-page {
  width: 100%;
  max-width: none;
  margin: 0;
  padding-bottom: clamp(12px, 2vh, 20px);
  min-height: calc(100vh - 108px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.health-ai-page__surface {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: min(78vh, 720px);
  overflow: hidden;
}

.health-ai-page__thread {
  flex: 1 1 0;
  min-height: 280px;
  overflow-y: auto;
  padding: clamp(20px, 3vw, 32px) clamp(16px, 4vw, 40px);
  -webkit-overflow-scrolling: touch;
}

.health-ai-page__welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: min(48vh, 420px);
  padding: 24px 16px 32px;
  text-align: center;
}

.health-ai-page__welcome-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  border-radius: 50%;
  background: rgba(42, 157, 111, 0.12);
  color: #2a9d6f;
  font-size: 30px;
}

.health-ai-page__welcome-title {
  margin: 0 0 10px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(1.4rem, 2.5vw, 1.75rem);
  font-weight: 600;
  color: var(--nb-ink, #24332b);
  letter-spacing: -0.02em;
}

.health-ai-page__welcome-text {
  margin: 0;
  max-width: 36rem;
  font-size: 15px;
  line-height: 1.55;
  color: rgba(36, 51, 43, 0.58);
}

.health-ai-page__welcome-label {
  margin: 28px 0 12px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(36, 51, 43, 0.45);
}

.health-ai-page__prompts {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  max-width: 40rem;
}

.health-ai-page__prompt {
  appearance: none;
  cursor: pointer;
  padding: 10px 16px;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.35;
  text-align: left;
  color: #355247;
  background: rgba(231, 246, 238, 0.65);
  border: 1px solid rgba(126, 197, 160, 0.4);
  border-radius: 12px;
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    transform 0.12s ease;

  &:hover:not(:disabled) {
    background: rgba(231, 246, 238, 1);
    border-color: rgba(42, 157, 111, 0.45);
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }
}

.health-ai-page__turn {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  max-width: 52rem;
}

.health-ai-page__turn.is-user {
  flex-direction: row-reverse;
  margin-left: auto;
}

.health-ai-page__turn.is-assistant,
.health-ai-page__turn.is-typing {
  margin-right: auto;
}

.health-ai-page__avatar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  font-size: 16px;

  &.is-user {
    background: #2a9d6f;
    color: #fff;
  }

  &.is-assistant {
    background: rgba(42, 157, 111, 0.14);
    color: #2a9d6f;
  }
}

.health-ai-page__content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  max-width: calc(100% - 48px);
}

.is-user .health-ai-page__content {
  align-items: flex-end;
}

.health-ai-page__role {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: rgba(36, 51, 43, 0.45);
}

.health-ai-page__bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.is-user .health-ai-page__bubble {
  background: #2a9d6f;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.is-assistant .health-ai-page__bubble,
.is-typing .health-ai-page__bubble {
  background: rgba(247, 251, 248, 0.98);
  color: #24332b;
  border: 1px solid rgba(126, 197, 160, 0.32);
  border-bottom-left-radius: 4px;
}

.health-ai-page__bubble--typing {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 14px 18px;
  min-width: 56px;
}

.health-ai-page__dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: rgba(42, 157, 111, 0.55);
  animation: health-ai-dot 1.2s ease-in-out infinite;

  &:nth-child(2) {
    animation-delay: 0.15s;
  }

  &:nth-child(3) {
    animation-delay: 0.3s;
  }
}

@keyframes health-ai-dot {
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

.health-ai-page__compose {
  flex-shrink: 0;
  padding: 14px clamp(16px, 4vw, 32px) 18px;
  border-top: 1px solid rgba(126, 197, 160, 0.18);
  background: rgba(255, 255, 255, 0.72);
}

.health-ai-page__compose :deep(.el-textarea__inner) {
  border-radius: 14px;
  border-color: rgba(126, 197, 160, 0.38);
  font-size: 15px;
  line-height: 1.5;
  min-height: 88px !important;
  padding: 12px 14px;
  background: #f8fcf9;

  &:focus {
    border-color: rgba(42, 157, 111, 0.55);
    box-shadow: 0 0 0 3px rgba(42, 157, 111, 0.12);
  }
}

.health-ai-page__actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.health-ai-page__btn {
  appearance: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font: inherit;
  font-size: 14px;
  font-weight: 650;
  padding: 10px 20px;
  border-radius: 999px;
  border: none;
  transition:
    opacity 0.15s ease,
    background 0.15s ease,
    transform 0.12s ease;

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  i {
    font-size: 15px;
  }
}

.health-ai-page__btn--ghost {
  margin-right: auto;
  background: transparent;
  color: #5c7268;
  border: 1px solid rgba(126, 197, 160, 0.45);

  &:hover:not(:disabled) {
    background: rgba(126, 197, 160, 0.12);
  }
}

.health-ai-page__btn--primary {
  background: #2a9d6f;
  color: #fff;
  box-shadow: 0 2px 10px rgba(42, 157, 111, 0.22);

  &:hover:not(:disabled) {
    background: #248760;
    transform: translateY(-1px);
  }
}

@media (max-width: 640px) {
  .health-ai-page {
    min-height: calc(100vh - 96px);
  }

  .health-ai-page__turn {
    max-width: 100%;
  }

  .health-ai-page__content {
    max-width: calc(100% - 44px);
  }

  .health-ai-page__btn--ghost {
    margin-right: 0;
  }
}
</style>
