ALTER TABLE `scenes` ADD `voiceProvider` varchar(40);--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceId` varchar(160);--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceName` varchar(160);--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceLanguage` varchar(40);--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceAudioUrl` varchar(1200);--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceAudioKey` varchar(600);--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceStatus` enum('none','generating','ready','failed') DEFAULT 'none' NOT NULL;--> statement-breakpoint
ALTER TABLE `scenes` ADD `voiceError` text;