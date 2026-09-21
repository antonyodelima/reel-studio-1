CREATE TABLE `mediaAssets` (
	`id` int AUTO_INCREMENT NOT NULL,
	`userId` int NOT NULL,
	`projectId` int,
	`fileName` varchar(255) NOT NULL,
	`mimeType` varchar(120) NOT NULL,
	`storageKey` varchar(600) NOT NULL,
	`storageUrl` varchar(1200) NOT NULL,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	CONSTRAINT `mediaAssets_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `projects` (
	`id` int AUTO_INCREMENT NOT NULL,
	`userId` int NOT NULL,
	`name` varchar(180) NOT NULL,
	`type` varchar(40) NOT NULL DEFAULT 'Reel',
	`preset` varchar(80) NOT NULL DEFAULT 'Product Launch',
	`format` varchar(30) NOT NULL DEFAULT '9:16 Portrait',
	`engine` enum('ffmpeg','remotion','hyperframes') NOT NULL DEFAULT 'ffmpeg',
	`status` enum('draft','rendering','ready','failed') NOT NULL DEFAULT 'draft',
	`sourceText` text,
	`sourceUrl` varchar(1000),
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	`updatedAt` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `projects_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `renderJobs` (
	`id` int AUTO_INCREMENT NOT NULL,
	`userId` int NOT NULL,
	`projectId` int NOT NULL,
	`engine` enum('ffmpeg','remotion','hyperframes') NOT NULL DEFAULT 'ffmpeg',
	`status` enum('queued','rendering','ready','failed') NOT NULL DEFAULT 'queued',
	`progress` int NOT NULL DEFAULT 0,
	`mp4Url` varchar(1200),
	`srtUrl` varchar(1200),
	`vttUrl` varchar(1200),
	`errorMessage` text,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	`updatedAt` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `renderJobs_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `scenes` (
	`id` int AUTO_INCREMENT NOT NULL,
	`projectId` int NOT NULL,
	`position` int NOT NULL,
	`label` varchar(80) NOT NULL,
	`title` text NOT NULL,
	`caption` text NOT NULL,
	`durationMs` int NOT NULL DEFAULT 5000,
	`mediaUrl` varchar(1200),
	`mediaKey` varchar(600),
	`locked` int NOT NULL DEFAULT 0,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	`updatedAt` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `scenes_id` PRIMARY KEY(`id`)
);
