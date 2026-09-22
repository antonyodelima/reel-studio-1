CREATE TABLE `voiceClones` (
	`id` int AUTO_INCREMENT NOT NULL,
	`userId` int NOT NULL,
	`cartesiaVoiceId` varchar(160),
	`name` varchar(160) NOT NULL,
	`language` varchar(20) NOT NULL,
	`tagline` varchar(80),
	`description` text,
	`sourceFileName` varchar(255) NOT NULL,
	`sourceMimeType` varchar(120) NOT NULL,
	`consentConfirmed` int NOT NULL DEFAULT 0,
	`status` enum('creating','ready','failed','deleted') NOT NULL DEFAULT 'creating',
	`errorMessage` text,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	`updatedAt` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `voiceClones_id` PRIMARY KEY(`id`)
);
