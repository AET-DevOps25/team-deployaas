<template>
  <div class="card bg-base-100 shadow-lg">
    <div class="card-body">
      <h3 class="card-title flex items-center gap-2">
        <InfoIcon class="w-5 h-5 text-info" />
        How to Import into Anki
      </h3>
      
      <div class="space-y-4">
        <div class="alert alert-info">
          <InfoIcon class="w-4 h-4" />
          <span>Follow these steps to import your flashcards into Anki for spaced repetition learning.</span>
        </div>

        <ol class="list-decimal list-inside space-y-2 text-sm">
          <li>
            <strong>Download Anki:</strong> 
            <a href="https://apps.ankiweb.net/" target="_blank" class="link link-primary">
              Get Anki from ankiweb.net
            </a>
          </li>
          <li>
            <strong>Open Anki</strong> and create a new deck (or select an existing one)
          </li>
          <li>
            Go to <code class="bg-base-200 px-1 rounded">File → Import</code>
          </li>
          <li>
            Select the downloaded <code class="bg-base-200 px-1 rounded">.txt</code> file
          </li>
          <li>
            In the import dialog:
            <ul class="list-disc list-inside ml-4 mt-1 space-y-1">
              <li>Set <strong>Field separator</strong> to "Tab"</li>
              <li>Map <strong>Field 1</strong> to "Front"</li>
              <li>Map <strong>Field 2</strong> to "Back"</li>
            </ul>
          </li>
          <li>
            Click <strong>Import</strong> to add your flashcards
          </li>
        </ol>

        <div class="alert alert-success">
          <CheckCircleIcon class="w-4 h-4" />
          <span>Your flashcards are now ready for spaced repetition study in Anki!</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  Info as InfoIcon,
  CheckCircle as CheckCircleIcon,
} from "lucide-vue-next";
</script>
