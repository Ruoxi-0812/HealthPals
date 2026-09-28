# Persistent uploads with Cloudinary

Set these environment variables on the Render backend, not on the frontend:

- `APP_STORAGE_PROVIDER=cloudinary`
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

Use the Free plan and a dedicated application credential. Do not commit actual
keys. Missing credentials in cloudinary mode fail startup. Upload errors return
failure and never fall back to Render's ephemeral disk.

The authenticated `/file/upload` and `/file/video/upload` endpoints retain their
existing JSON response format, returning a Cloudinary HTTPS URL in `data`.
The frontend saves this URL in the same database fields as before. Objects use
random IDs under `healthpals/` and cannot overwrite prior objects. Existing local
file retrieval remains available for files still present on disk.

This change affects new uploads. It cannot recover already-lost Render files or
AWS uploads. The repository's static article images are served by Vercel and do
not need migration. Existing external avatar URLs remain unchanged.

Images use Cloudinary's image resource type and videos use its video resource
type, subject to the existing 10 MB request limit. This is public media storage
for avatars and site illustrations, not a private document vault. Uploaded files
remain until separately deleted; automatic cleanup of replaced images is not
implemented. Storage and delivery count toward the account's shared free quota.

Validation: CloudinaryStorageServiceTest covers URL handling, provider failures,
empty inputs, required configuration, and controller failure without local disk
fallback. A real deployed upload and a post-restart fetch must also pass before
claiming that production persistence is verified.

## Folder permissions

Uploads use the `healthpals` asset folder (dynamic folder mode). The dedicated API key has the Media Library User role plus Contributor access to this folder, assigned through the Cloudinary Admin API. No management credential is deployed to Render.

## Production verification (2026-09-28)

Deployment `0fcb27f` is live on Render. An isolated account successfully uploaded the project logo through the production Vercel API proxy and saved the returned Cloudinary URL as its avatar. The backend was manually restarted and the replacement instance started successfully. Automated tests: 46 passed, zero failures/errors.

After the old instance shut down, a fresh authenticated request returned the same saved avatar URL and fetching that image returned HTTP 200.
