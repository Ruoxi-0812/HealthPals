<template>
  <div class="health-ai-page">
    <header class="health-ai-page__head">
      <span class="health-ai-page__eyebrow">Assistant</span>
      <h1 class="health-ai-page__title">Ask Pal</h1>
      <p class="health-ai-page__disclaimer">
        General wellness information only—not medical advice. For emergencies
        or serious symptoms, contact emergency services or a qualified clinician.
      </p>
    </header>

    <section class="health-ai-page__surface">
      <div ref="threadEl" class="health-ai-page__thread">
        <p v-if="thread.length === 0" class="health-ai-page__hint">
          Ask about sleep, nutrition, stress, movement, or when it might make
          sense to see a doctor.
        </p>
        <div
          v-for="(turn, idx) in thread"
          :key="'t-' + idx + '-' + turn.role"
          class="health-ai-page__turn"
          :class="'is-' + turn.role"
        >
          <span class="health-ai-page__role">{{
            turn.role === "user" ? "You" : "Assistant"
          }}</span>
          <div class="health-ai-page__bubble">{{ turn.text }}</div>
        </div>
      </div>

      <div class="health-ai-page__compose">
        <el-input
          v-model="input"
          type="textarea"
          :rows="5"
          resize="none"
          placeholder="Describe how you feel or what you'd like to know…"
          :disabled="loading"
          @keydown.native.enter.exact.prevent="send"
        />
        <div class="health-ai-page__actions">
          <button
            type="button"
            class="health-ai-page__btn health-ai-page__btn--ghost"
            :disabled="loading"
            @click="clearChat"
          >
            Clear chat
          </button>
          <button
            type="button"
            class="health-ai-page__btn health-ai-page__btn--primary"
            :disabled="loading || !input.trim()"
            @click="send"
          >
            <template v-if="loading">Thinking…</template>
            <template v-else>Send</template>
          </button>
        </div>
      </div>
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
    };
  },
  methods: {
    scrollToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.threadEl;
        if (el) el.scrollTop = el.scrollHeight;
      });
    },
    clearChat() {
      this.thread = [];
      this.input = "";
    },
    async send() {
      const text = (this.input || "").trim();
      if (!text || this.loading) return;
      this.loading = true;
      try {
        const history = this.thread.map((t) => ({
          role: t.role,
          content: t.text,
        }));
        const response = await this.$axios.post(
          "/health-assistant/chat",
          { message: text, history },
          { timeout: 125000 },
        );
        const { data } = response;
        if (data.code === 200 && data.data && data.data.reply) {
          this.thread.push({ role: "user", text });
          this.thread.push({ role: "assistant", text: data.data.reply });
          this.input = "";
          this.scrollToBottom();
        } else {
          this.$message.error(data.msg || "Something went wrong.");
        }
      } catch (e) {
        console.error(e);
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
/* Wider, taller panel — conversation fills viewport like Claude */
.health-ai-page {
  width: 100%;
  max-width: min(1040px, 96vw);
  margin: 0 auto;
  padding-bottom: clamp(16px, 3vh, 28px);
  min-height: calc(100vh - 108px);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.health-ai-page__head {
  flex-shrink: 0;
  margin-bottom: 14px;
}

.health-ai-page__eyebrow {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(36, 51, 43, 0.52);
  margin-bottom: 6px;
}

.health-ai-page__title {
  margin: 0 0 8px;
  font-family: var(--nb-font-display, Georgia, serif);
  font-size: clamp(1.35rem, 2vw, 1.65rem);
  font-weight: 600;
  color: var(--nb-ink, #24332b);
  letter-spacing: -0.02em;
}

.health-ai-page__disclaimer {
  margin: 0;
  font-size: 13px;
  line-height: 1.45;
  color: var(--nb-muted, rgba(36, 51, 43, 0.62));
  max-width: min(65ch, 100%);
}

.health-ai-page__surface {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: min(70vh, 640px);
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(126, 197, 160, 0.22);
  border-radius: 20px;
  box-shadow: 0 18px 48px rgba(53, 92, 75, 0.12);
  overflow: hidden;
}

.health-ai-page__thread {
  flex: 1 1 0;
  min-height: 0;
  overflow-y: auto;
  padding: clamp(16px, 2.5vw, 28px) clamp(18px, 3vw, 36px);
  border-bottom: 1px solid rgba(126, 197, 160, 0.18);
  -webkit-overflow-scrolling: touch;
}

.health-ai-page__hint {
  margin: 4px 0 8px;
  font-size: 15px;
  line-height: 1.6;
  color: rgba(36, 51, 43, 0.52);
  max-width: 42rem;
}

.health-ai-page__turn {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.health-ai-page__turn.is-user {
  align-items: flex-end;
}

.health-ai-page__turn.is-assistant {
  align-items: flex-start;
}

.health-ai-page__role {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: rgba(36, 51, 43, 0.45);
}

.health-ai-page__bubble {
  max-width: min(100%, 42rem);
  padding: 14px 18px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.is-user .health-ai-page__bubble {
  max-width: min(100%, 36rem);
  background: #2a9d6f;
  color: #fff;
  border-bottom-right-radius: 6px;
}

.is-assistant .health-ai-page__bubble {
  background: rgba(247, 251, 248, 0.98);
  color: #24332b;
  border: 1px solid rgba(126, 197, 160, 0.32);
  border-bottom-left-radius: 6px;
}

.health-ai-page__compose {
  flex-shrink: 0;
  padding: clamp(14px, 2vw, 22px) clamp(18px, 3vw, 36px)
    clamp(18px, 2.5vw, 26px);
  background: rgba(255, 255, 255, 0.55);
}

.health-ai-page__compose :deep(.el-textarea__inner) {
  border-radius: 16px;
  border-color: rgba(126, 197, 160, 0.38);
  font-size: 15px;
  line-height: 1.55;
  min-height: 120px;
  padding: 14px 16px;
}

.health-ai-page__actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 14px;
  flex-wrap: wrap;
}

.health-ai-page__btn {
  appearance: none;
  cursor: pointer;
  font: inherit;
  font-size: 14px;
  font-weight: 650;
  padding: 11px 22px;
  border-radius: 999px;
  border: none;
  transition:
    opacity 0.15s ease,
    background 0.15s ease;

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.health-ai-page__btn--ghost {
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

  &:hover:not(:disabled) {
    background: #248760;
  }
}

@media (max-width: 640px) {
  .health-ai-page {
    min-height: calc(100vh - 100px);
    max-width: 100%;
  }

  .health-ai-page__bubble {
    max-width: 100%;
  }

  .is-user .health-ai-page__bubble {
    max-width: 100%;
  }
}
</style>
